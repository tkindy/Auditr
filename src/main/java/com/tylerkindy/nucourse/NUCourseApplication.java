package com.tylerkindy.nucourse;

import com.hubspot.dropwizard.guice.GuiceBundle;
import com.netflix.governator.guice.LifecycleInjector;
import com.palantir.websecurity.WebSecurityBundle;
import com.tylerkindy.nucourse.config.S3ConfigurationProvider;
import com.tylerkindy.nucourse.jobs.CatalogScrapingJob;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.JobsBundle;
import io.dropwizard.Application;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import software.amazon.awssdk.auth.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class NUCourseApplication extends Application<NUCourseConfiguration> {

  private static final String AWS_PROFILE = "nucourse";

  public static void main(final String[] args) throws Exception {
    new NUCourseApplication().run(args);
  }

  @Override
  public void initialize(final Bootstrap<NUCourseConfiguration> bootstrap) {
    setConfigProvider(bootstrap);
    addGuiceBundle(bootstrap);

    bootstrap.addBundle(new WebSecurityBundle());

    initializeJobs(bootstrap);
  }

  private static void setConfigProvider(Bootstrap<NUCourseConfiguration> bootstrap) {
    ConfigurationSourceProvider provider =
        new S3ConfigurationProvider(S3Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.builder()
                .profileName(AWS_PROFILE)
                .build())
            .build());

    bootstrap.setConfigurationSourceProvider(provider);
  }

  private void addGuiceBundle(Bootstrap<NUCourseConfiguration> bootstrap) {
    GuiceBundle<NUCourseConfiguration> guiceBundle = GuiceBundle.<NUCourseConfiguration>newBuilder()
        .addModule(new NUCourseModule())
        .enableAutoConfig(getClass().getPackage().getName())
        .setConfigClass(NUCourseConfiguration.class)
        .setInjectorFactory((stage, modules) -> LifecycleInjector.builder()
            .inStage(stage)
            .withModules(modules)
            .build()
            .createInjector())
        .build();

    bootstrap.addBundle(guiceBundle);
  }

  private void initializeJobs(Bootstrap<NUCourseConfiguration> bootstrap) {
    Job catalogScrapingJob = new CatalogScrapingJob();

    bootstrap.addBundle(new JobsBundle(catalogScrapingJob));
  }

  @Override
  public void run(final NUCourseConfiguration configuration,
      final Environment environment) {
    // auto-discovers resources
  }
}
