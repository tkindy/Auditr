package com.tylerkindy.nucourse.jobs;

import com.google.inject.Inject;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import okhttp3.OkHttpClient;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Every
public class CatalogScrapingJob extends Job {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogScrapingJob.class);

  private final OkHttpClient httpClient;

  @Inject
  CatalogScrapingJob(OkHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public void doJob(JobExecutionContext context) throws JobExecutionException {
    /*
    For each term:
      For each subject:
        For each course:
          - parse
          - store
     */

  }
}
