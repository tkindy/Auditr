package com.tylerkindy.nucourse;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.hubspot.dropwizard.guice.GuiceBundle;
import com.netflix.governator.guice.LifecycleInjector;
import com.tylerkindy.nucourse.config.S3ConfigurationProvider;

import io.dropwizard.Application;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class NUCourseApplication extends Application<NUCourseConfiguration> {

  public static void main(final String[] args) throws Exception {
    new NUCourseApplication().run(args);
  }

  @Override
  public void initialize(final Bootstrap<NUCourseConfiguration> bootstrap) {
    setConfigProvider(bootstrap);
    addGuiceBundle(bootstrap);
  }

  private static void setConfigProvider(Bootstrap<NUCourseConfiguration> bootstrap) {
    ConfigurationSourceProvider provider =
        new S3ConfigurationProvider(AmazonS3ClientBuilder.defaultClient());

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

  @Override
  public void run(final NUCourseConfiguration configuration,
                  final Environment environment) {
    // auto-discovers resources
  }
}
