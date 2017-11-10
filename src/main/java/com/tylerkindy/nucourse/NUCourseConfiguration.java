package com.tylerkindy.nucourse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.palantir.websecurity.WebSecurityConfigurable;
import com.palantir.websecurity.WebSecurityConfiguration;
import de.spinscale.dropwizard.jobs.JobConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class NUCourseConfiguration extends Configuration
    implements WebSecurityConfigurable, JobConfiguration {

  @JsonProperty("database")
  @Valid
  @NotNull
  private DataSourceFactory database = new DataSourceFactory();

  public void setDataSourceFactory(DataSourceFactory factory) {
    this.database = factory;
  }

  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  @JsonProperty("webSecurity")
  @Valid
  @NotNull
  private final WebSecurityConfiguration webSecurity = WebSecurityConfiguration.DEFAULT;

  public WebSecurityConfiguration getWebSecurityConfiguration() {
    return this.webSecurity;
  }

  @JsonProperty("jobs")
  @Valid
  @NotNull
  private Map<String, String> jobs;

  public Map<String, String> getJobs() {
    return jobs;
  }

  public void setJobs(Map<String, String> jobs) {
    this.jobs = jobs;
  }
}
