package com.tylerkindy.auditr;

import com.hubspot.dropwizard.guice.GuiceBundle;
import com.netflix.governator.guice.LifecycleInjector;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AuditrApplication extends Application<AuditrConfiguration> {

  public static void main(final String[] args) throws Exception {
    new AuditrApplication().run(args);
  }

  @Override
  public String getName() {
    return "Auditr";
  }

  @Override
  public void initialize(final Bootstrap<AuditrConfiguration> bootstrap) {
    GuiceBundle<AuditrConfiguration> guiceBundle = GuiceBundle.<AuditrConfiguration>newBuilder()
        .addModule(new AuditrModule())
        .enableAutoConfig(getClass().getPackage().getName())
        .setConfigClass(AuditrConfiguration.class)
        .setInjectorFactory((stage, modules) -> LifecycleInjector.builder()
            .inStage(stage)
            .withModules(modules)
            .build()
            .createInjector())
        .build();

    bootstrap.addBundle(guiceBundle);
  }

  @Override
  public void run(final AuditrConfiguration configuration,
      final Environment environment) {
    // auto-discovers resources
  }

}
