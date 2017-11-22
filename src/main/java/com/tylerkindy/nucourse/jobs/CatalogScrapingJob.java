package com.tylerkindy.nucourse.jobs;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.tylerkindy.nucourse.core.catalog.Course;
import com.tylerkindy.nucourse.core.catalog.Term;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Every
public class CatalogScrapingJob extends Job {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogScrapingJob.class);
  private static final String CATALOG_BASE_URL = "https://wl11gp.neu.edu/udcprod8/bwckctlg.";
  private static final MediaType FORM_ENC = MediaType.parse("application/x-www-form-urlencoded");

  private static final Pattern COURSE_TITLE =
      Pattern.compile("^([A-Z]{1,4}) (\\d{3,4}) - (.*)$");
  private static final Pattern HOURS = Pattern.compile("^(\\d+\\.\\d+) .*$");
  private static final Pattern LEVELS = Pattern.compile("^Levels: (.*)$");
  private static final Pattern SCHEDULE_TYPES = Pattern.compile("^Schedule Types: (.*)$");
  private static final Pattern DEPT = Pattern.compile("^(.*) Department$");

  private static final String GET_SUBJS_ERROR = "Error getting subjects for %s";
  private static final String GET_COURSES_ERROR = "Error getting courses for %s in %s";

  private static final Set<Integer> YEARS = Sets.newHashSet(2017);

  private final OkHttpClient httpClient;

  @Inject
  CatalogScrapingJob(OkHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public void doJob(JobExecutionContext context) throws JobExecutionException {
    for (int year : YEARS) {
      for (Term term : Term.values()) {
        String termIn = term.getTermIn(year);
        Set<String> subjects = getSubjects(termIn);

        for (String subject : subjects) {
          scrapeCourses(termIn, subject);
        }

        LOG.info("Scraped all courses for {}", termIn);
      }
    }
  }

  private Set<String> getSubjects(String termIn) {
    String formData = buildSubjectForm(termIn);
    Document doc = getFormResponse("p_disp_cat_term_date", formData,
        String.format(GET_SUBJS_ERROR, termIn));

    Elements options = doc.select("select[name=sel_subj] > option");

    return options.stream()
        .map((el) -> el.attr("value"))
        .collect(Collectors.toSet());
  }

  private static String buildSubjectForm(String termIn) {
    return String.format("call_proc_in=bwckctlg.p_disp_dyn_ctlg&cat_term_in=%s", termIn);
  }

  private void scrapeCourses(String termIn, String subject) {
    LOG.info("Scraping {} in {}", subject, termIn);
    String formData = buildCoursesForm(termIn, subject);
    Document doc = getFormResponse("p_display_courses", formData,
        String.format(GET_COURSES_ERROR, subject, termIn));

    Elements cells = doc.select(
        "div.pagebodydiv > table.datadisplaytable > tbody > tr > td");

    for (int i = 0; i < cells.size(); i += 2) {
      scrapeCourse(cells.get(i), cells.get(i + 1));
    }
  }

  private static String buildCoursesForm(String termIn, String subject) {
    return String.format("term_in=%s&call_proc_in=bwckctlg.p_disp_dyn_ctlg&"
        + "sel_subj=dummy&sel_levl=dummy&sel_schd=dummy&sel_coll=dummy&sel_divs=dummy&"
        + "sel_dept=dummy&sel_attr=dummy&sel_subj=%s&sel_crse_strt=&sel_crse_end=&"
        + "sel_title=&sel_levl=%%25&sel_schd=%%25&sel_coll=%%25&sel_divs=%%25&sel_dept=%%25&"
        + "sel_from_cred=&sel_to_cred=&sel_attr=%%25", termIn, subject);
  }

  private Document getFormResponse(String path, String formData, String errorMsg) {
    RequestBody reqBody = RequestBody.create(FORM_ENC, formData);

    Request req = new Request.Builder()
        .url(CATALOG_BASE_URL + path)
        .post(reqBody)
        .build();

    try (Response response = httpClient.newCall(req).execute()) {
      ResponseBody body = response.body();

      if (body == null) {
        throw new RuntimeException(errorMsg);
      }

      return Jsoup.parse(body.string());

    } catch (IOException e) {
      throw new RuntimeException(errorMsg, e);
    }
  }

  private static Course scrapeCourse(Element titleCell, Element bodyCell) {
    Course.Builder builder = Course.builder();

    Matcher titleMatcher = COURSE_TITLE.matcher(titleCell.text());
    String subject;
    int number;
    String name;

    if (titleMatcher.matches()) {
      subject = titleMatcher.group(1);
      LOG.info("Subject: " + subject);
      builder.setSubject(subject);

      number = Integer.parseInt(titleMatcher.group(2));
      LOG.info("Number: " + Integer.toString(number));
      builder.setNumber(number);

      name = titleMatcher.group(3);
      LOG.info("Name: " + name);
      builder.setName(name);
    } else {
      throw new RuntimeException(String.format("Can't parse title '%s'", titleCell.text()));
    }

    // TODO schematize body to avoid indexing issues
    // search each line to find each attribute?
    List<String> lines = Stream.of(
        Parser.unescapeEntities(bodyCell.html(), true)
            .split("<br>"))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());

    String description = lines.get(0);
    LOG.info("Description: " + description);
    builder.setDescription(description);

    Matcher creditHoursMatcher = HOURS.matcher(lines.get(1));
    double creditHours = creditHoursMatcher.matches() ?
        Double.parseDouble(creditHoursMatcher.group(1)) :
        -1;
    LOG.info("Credit Hours: " + Double.toString(creditHours));
    builder.setCreditHours(creditHours);

    Matcher scheduleHoursMatcher = HOURS.matcher(lines.get(2));
    double scheduleHours = scheduleHoursMatcher.matches() ?
        Double.parseDouble(scheduleHoursMatcher.group(1)) :
        -1;
    LOG.info("Schedule Hours: " + Double.toString(scheduleHours));
    builder.setScheduleHours(scheduleHours);

    Document levelsDoc = Jsoup.parseBodyFragment(lines.get(3));
    Matcher levelsMatcher = LEVELS.matcher(levelsDoc.text());
    Set<String> levels = levelsMatcher.matches() ?
        new HashSet<>(Arrays.asList(levelsMatcher.group(1).split(", "))) :
        Collections.emptySet();
    LOG.info("Levels: " + levels);
    builder.setLevels(levels);

    Document scheduleTypesDoc = Jsoup.parseBodyFragment(lines.get(4));
    Matcher scheduleTypesMatcher = SCHEDULE_TYPES.matcher(scheduleTypesDoc.text());/docker/dock
    Set<String> scheduleTypes = scheduleTypesMatcher.matches() ?
        new HashSet<>(Arrays.asList(scheduleTypesMatcher.group(1).split(", "))) :
        Collections.emptySet();
    LOG.info("Schedule Types: " + scheduleTypes);
    builder.setScheduleTypes(scheduleTypes);

    Matcher deptMatcher = DEPT.matcher(lines.get(5));
    String department = deptMatcher.matches() ?
        deptMatcher.group(1) :
        "";
    LOG.info("Department: " + department);
    builder.setDepartment(department);

    if (subject.equals("XLVL")) {  // TODO replace
      builder.setAttributes(new HashSet<>());
    } else {
      Set<String> attributes = new HashSet<>(Arrays.asList(lines.get(7).split(", ")));
      LOG.info("Attributes: " + attributes);
      builder.setAttributes(attributes);
    }

    // TODO store in database

    return builder.build();
  }
}
