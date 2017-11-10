package com.tylerkindy.nucourse;

import com.google.inject.Provides;
import com.hubspot.rosetta.jdbi.RosettaMapperFactory;
import com.netflix.governator.guice.lazy.LazySingleton;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public final class NUCourseRunModule extends NUCourseInitModule {

  @Override
  protected void configure() {
  }

  @Provides
  @LazySingleton
  DBI providesDbi(DBIFactory factory, Environment environment,
      NUCourseConfiguration configuration, RosettaMapperFactory rosettaMapperFactory) {
    DBI dbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
    dbi.registerMapper(rosettaMapperFactory);

    return dbi;
  }
}
