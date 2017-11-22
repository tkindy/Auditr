package com.tylerkindy.nucourse.core.catalog;

import com.tylerkindy.nucourse.core.MyStyle;
import java.util.Set;
import org.immutables.value.Value.Immutable;

@Immutable
@MyStyle
public interface CourseIF {
  String getSubject();
  int getNumber();
  String getName();
  String getDescription();
  double getCreditHours();
  double getScheduleHours();
  Set<String> getLevels();
  Set<String> getScheduleTypes();
  String getDepartment();
  Set<String> getAttributes();
}
