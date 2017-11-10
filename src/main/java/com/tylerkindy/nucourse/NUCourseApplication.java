package com.tylerkindy.nucourse;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hubspot.dropwizard.guice.GuiceBundle;
import com.netflix.governator.guice.LifecycleInjector;
import com.palantir.websecurity.WebSecurityBundle;
import com.tylerkindy.nucourse.jobs.CatalogScrapingJob;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.JobsBundle;
import io.dropwizard.Application;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class NUCourseApplication extends Application<NUCourseConfiguration> {

  private final Injector initInjector;

  private NUCourseApplication() {
    initInjector = Guice.createInjector(new NUCourseInitModule());
  }

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

  private void setConfigProvider(Bootstrap<NUCourseConfiguration> bootstrap) {
    bootstrap.setConfigurationSourceProvider(
        initInjector.getInstance(ConfigurationSourceProvider.class));
  }

  private void addGuiceBundle(Bootstrap<NUCourseConfiguration> bootstrap) {
    GuiceBundle<NUCourseConfiguration> guiceBundle = GuiceBundle.<NUCourseConfiguration>newBuilder()
        .addModule(new NUCourseRunModule())
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
    Job catalogScrapingJob = initInjector.getInstance(CatalogScrapingJob.class);

    bootstrap.addBundle(new JobsBundle(catalogScrapingJob));
  }

  @Override
  public void run(final NUCourseConfiguration configuration,
      final Environment environment) {
    // auto-discovers resources
  }
}
