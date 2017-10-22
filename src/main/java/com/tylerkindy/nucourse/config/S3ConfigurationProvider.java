package com.tylerkindy.nucourse.config;

import com.google.inject.Inject;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import jdk.internal.util.xml.impl.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.sync.StreamingResponseHandler;

public class S3ConfigurationProvider implements ConfigurationSourceProvider {

  private static final Logger LOG = LoggerFactory.getLogger(S3ConfigurationProvider.class);
  private static final String CONFIG_S3_BUCKET = "nucourse";
  private static final int MAX_BYTES = 10000;

  private final S3Client s3Client;

  @Inject
  public S3ConfigurationProvider(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  @Override
  public InputStream open(String path) throws IOException {
    DeployEnvironment environment = DeployEnvironment.getCurrentEnv();
    String configS3Key = "config/" + environment.getConfigFilename();

    LOG.warn("Fetching {}...", configS3Key);
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(CONFIG_S3_BUCKET)
        .key(configS3Key)
        .build();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(MAX_BYTES);
    s3Client.getObject(request, StreamingResponseHandler.toOutputStream(outputStream));
    LOG.warn("Fetched {}!", configS3Key);

    return new ByteArrayInputStream(outputStream.toByteArray());
  }
}
