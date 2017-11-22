package com.tylerkindy.nucourse.core.catalog;

public enum Term {
  FALL(10),
  SPRING(30);

  private final int termNum;

  Term(int termNum) {
    this.termNum = termNum;
  }

  public String getTermIn(int year) {
    return String.format("%d%d", year, termNum);
  }
}
