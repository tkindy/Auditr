package com.tylerkindy.nucourse;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.tylerkindy.nucourse.config.S3ConfigurationProvider;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import software.amazon.awssdk.auth.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class NUCourseInitModule extends AbstractModule {

  private static final String AWS_PROFILE = "nucourse";

  @Override
  protected void configure() {
  }

  @Provides
  OkHttpClient provideHttpClient() {
    return new OkHttpClient.Builder()
        .readTimeout(1, TimeUnit.MINUTES)
        .build();
  }

  @Provides
  @Singleton
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
