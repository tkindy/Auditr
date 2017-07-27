package com.tylerkindy.nucourse.core;

import java.time.LocalDate;
import org.immutables.value.Value.Immutable;

@Immutable
@MyStyle
public interface StudentIF {
  String getNuid();
  String getFirstName();
  String getLastName();
  LocalDate getGraduationDate();
}
