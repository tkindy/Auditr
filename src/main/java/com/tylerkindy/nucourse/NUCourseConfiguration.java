package com.tylerkindy.nucourse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.palantir.websecurity.WebSecurityConfigurable;
import com.palantir.websecurity.WebSecurityConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class NUCourseConfiguration extends Configuration implements WebSecurityConfigurable {
  @Valid
  @NotNull
  private DataSourceFactory database = new DataSourceFactory();

  @JsonProperty("database")
  public void setDataSourceFactory(DataSourceFactory factory) {
    this.database = factory;
  }

  @JsonProperty("database")
  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  @JsonProperty("webSecurity")
  @NotNull
  @Valid
  private final WebSecurityConfiguration webSecurity = WebSecurityConfiguration.DEFAULT;

  public WebSecurityConfiguration getWebSecurityConfiguration() {
    return this.webSecurity;
  }
}
