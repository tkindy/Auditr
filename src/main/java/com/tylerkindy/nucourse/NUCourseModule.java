package com.tylerkindy.nucourse;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hubspot.rosetta.jdbi.RosettaMapperFactory;
import com.netflix.governator.guice.lazy.LazySingleton;
import com.tylerkindy.nucourse.audit.AuditParser;
import com.tylerkindy.nucourse.db.daos.CatalogCourseDao;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.skife.jdbi.v2.DBI;

public class NUCourseModule extends AbstractModule {

  public static final String HTTP_CLIENT = "courses.service.httpClient";
  public static final String OBJECT_MAPPER = "courses.service.objectMapper";

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

  @Provides
  @LazySingleton
  CatalogCourseDao providesCatalogCourseDao(DBI dbi) {
    return dbi.onDemand(CatalogCourseDao.class);
  }

  @Provides
  @Named(HTTP_CLIENT)
  HttpClient provideHttpClient() {
    return HttpClients.createDefault();
  }

  @Provides
  @Singleton
  @Named(OBJECT_MAPPER)
  ObjectMapper provideObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();

    mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, false);
    mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, true);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    return mapper;
  }
}
