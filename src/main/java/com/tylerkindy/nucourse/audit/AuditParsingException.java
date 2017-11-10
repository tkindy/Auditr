package com.tylerkindy.nucourse.audit;

class AuditParsingException extends RuntimeException {

  AuditParsingException(String element, String value) {
    super(String.format("%s '%s' could not be parsed", element, value));
  }
}
