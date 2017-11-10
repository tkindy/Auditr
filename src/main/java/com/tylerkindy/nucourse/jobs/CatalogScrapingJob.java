package com.tylerkindy.nucourse.jobs;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Every
public class CatalogScrapingJob extends Job {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogScrapingJob.class);

  @Override
  public void doJob(JobExecutionContext context) throws JobExecutionException {
    LOG.warn("Running scraping job");
  }
}
