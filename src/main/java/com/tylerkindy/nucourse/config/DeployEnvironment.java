package com.tylerkindy.nucourse.config;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DeployEnvironment {
  DEV("dev-config.yml"),
  QA("qa-config.yml"),
  PROD("prod-config.yml");

  private static final Logger LOG = LoggerFactory.getLogger(DeployEnvironment.class);
  private static final DeployEnvironment DEFAULT_ENV = DEV;
  private static final String DEPLOY_ENV_ENV_VAR = "DEPLOY_ENV";
  private static Optional<DeployEnvironment> cachedEnv = Optional.empty();

  private final String configS3Key;

  DeployEnvironment(String configS3Key) {
    this.configS3Key = configS3Key;
  }

  static DeployEnvironment getCurrentEnv() {
    if (cachedEnv.isPresent()) {
      return cachedEnv.get();
    }

    Optional<String> maybeEnvStr = Optional.ofNullable(System.getenv(DEPLOY_ENV_ENV_VAR));

    if (!maybeEnvStr.isPresent()) {
      LOG.warn("{} not set, defaulting to {}", DEPLOY_ENV_ENV_VAR, DEFAULT_ENV);
      return DEFAULT_ENV;
    }

    String envStr = maybeEnvStr.get();

    try {
      DeployEnvironment env = DeployEnvironment.valueOf(envStr);

      cachedEnv = Optional.of(env);
      return env;
    } catch (IllegalArgumentException e) {
      LOG.warn("{} not recognized, defaulting to {}", envStr, DEFAULT_ENV);
      return DEFAULT_ENV;
    }
  }

  public String getConfigS3Key() {
    return configS3Key;
  }
}
