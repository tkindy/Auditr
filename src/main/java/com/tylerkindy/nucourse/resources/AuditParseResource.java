package com.tylerkindy.nucourse.resources;

import com.google.inject.Inject;
import com.tylerkindy.nucourse.audit.AuditParser;
import com.tylerkindy.nucourse.core.Audit;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("parse-audit")
public class AuditParseResource {

  private final AuditParser auditParser;

  @Inject
  public AuditParseResource(AuditParser auditParser) {
    this.auditParser = auditParser;
  }

  @POST
  @Consumes(MediaType.TEXT_HTML)
  @Produces(MediaType.APPLICATION_JSON)
  public Audit parseAudit(String html) {
    return auditParser.parse(html);
  }
}
