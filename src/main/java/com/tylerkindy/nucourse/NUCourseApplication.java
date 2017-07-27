package com.tylerkindy.nucourse;

import com.hubspot.dropwizard.guice.GuiceBundle;
import com.netflix.governator.guice.LifecycleInjector;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class NUCourseApplication extends Application<NUCourseConfiguration> {

  public static void main(final String[] args) throws Exception {
    new NUCourseApplication().run(args);
  }

  @Override
  public String getName() {
    return "NUCourse";
  }

  @Override
  public void initialize(final Bootstrap<NUCourseConfiguration> bootstrap) {
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
