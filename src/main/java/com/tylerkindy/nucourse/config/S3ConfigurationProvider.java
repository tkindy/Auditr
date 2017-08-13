package com.tylerkindy.nucourse.config;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.google.inject.Inject;

import io.dropwizard.configuration.ConfigurationSourceProvider;

public class S3ConfigurationProvider implements ConfigurationSourceProvider {

  private static final Logger LOG = LoggerFactory.getLogger(S3ConfigurationProvider.class);
  private static final String CONFIG_S3_BUCKET = "nucourse";

  private final AmazonS3 s3Client;

  @Inject
  public S3ConfigurationProvider(AmazonS3 s3Client) {
    this.s3Client = s3Client;
  }

  @Override
  public InputStream open(String path) throws IOException {
    DeployEnvironment environment = DeployEnvironment.getCurrentEnv();
    String configS3Key = "config/" + environment.getConfigFilename();

    LOG.warn("Fetching {}...", configS3Key);
    S3Object config = s3Client.getObject(CONFIG_S3_BUCKET, configS3Key);
    LOG.warn("Fetched {}!", configS3Key);

    return config.getObjectContent();
  }
}
