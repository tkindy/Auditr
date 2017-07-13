package com.tylerkindy.auditr.audit;

import com.google.inject.Inject;
import com.tylerkindy.auditr.core.CatalogCourse;
import com.tylerkindy.auditr.db.repos.CatalogCourseRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditParser {

  private static final Logger LOG = LoggerFactory.getLogger(AuditParser.class);
  private static final Pattern SECTION_HEADER_PATTERN = Pattern.compile("^(NO|IP|OK)\\s+(.+)$");
  private static final Pattern STARTED_COURSE_LINE_PATTERN = Pattern.compile("^((?:FL|SP|S1|S2)\\d{2}) (\\w+?)\\s*(\\d+).*$");

  private final CatalogCourseRepository catalogCourseRepository;

  @Inject
  public AuditParser(CatalogCourseRepository catalogCourseRepository) {
    this.catalogCourseRepository = catalogCourseRepository;
  }

  public Collection<CatalogCourse> parse(String html) {
    return parse(Jsoup.parse(html));
  }

  private Collection<CatalogCourse> parse(Document document) {
    return document.select("a[href=\"#linkback\"]").stream()
        .map(this::parseSection)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  private Collection<CatalogCourse> parseSection(Element backLink) {
    Collection<CatalogCourse> startedCourses = new HashSet<>();

    Element headerElement = backLink.nextElementSibling();
    String sectionHeader = headerElement.text().trim();
    Matcher sectionHeaderMatcher = SECTION_HEADER_PATTERN.matcher(sectionHeader);

    if (sectionHeaderMatcher.matches()) {
      Element element = headerElement;
      while ((element = element.nextElementSibling()).is("p")) {
        if (element.children().isEmpty()) {
          continue;
        }

        element.select("i").remove();  // remove extra info
        Elements startedCourseLines = element.select("font");

        for (Element startedCourseElement : startedCourseLines) {
          String line = startedCourseElement.text().trim();
          Matcher matcher = STARTED_COURSE_LINE_PATTERN.matcher(line);

          if (matcher.matches()) {
            String subject = matcher.group(2);
            int number = Integer.parseInt(matcher.group(3));

            Optional<CatalogCourse> maybeCourse = catalogCourseRepository.getBySubNum(subject, number);

            if (maybeCourse.isPresent()) {
              startedCourses.add(maybeCourse.get());
            } else {
              LOG.warn("{} {} wasn't found in the database", subject, number);
            }
          }
        }
      }
    }

    return startedCourses;
  }
}
