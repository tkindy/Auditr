package com.tylerkindy.auditr.core;

import org.immutables.value.Value.Immutable;

@Immutable
@MyStyle
public interface RequirementIF extends RequirementCore {
  RemainingCoursesOperator getRemainingCoursesOperator();
}
