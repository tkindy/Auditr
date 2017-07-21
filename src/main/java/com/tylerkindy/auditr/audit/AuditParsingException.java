package com.tylerkindy.auditr.audit;

public class AuditParsingException extends RuntimeException {
  public AuditParsingException(String element, String value) {
    super(String.format("%s '%s' could not be parsed", element, value));
  }
}
