package com.tylerkindy.nucourse;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.tylerkindy.nucourse.config.S3ConfigurationProvider;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import software.amazon.awssdk.auth.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class NUCourseInitModule extends AbstractModule {

  public static final String HTTP_CLIENT = "courses.service.httpClient";
  public static final String OBJECT_MAPPER = "courses.service.objectMapper";

  private static final String AWS_PROFILE = "nucourse";

  @Override
  protected void configure() {
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

  @Provides
  @Singleton
  ConfigurationSourceProvider provideConfigurationSourceProvider(S3Client s3Client) {
    return new S3ConfigurationProvider(s3Client);
  }

  @Provides
  @Singleton
  S3Client provideS3Client() {
    return S3Client.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(ProfileCredentialsProvider.builder()
            .profileName(AWS_PROFILE)
            .build())
        .build();
  }
}
