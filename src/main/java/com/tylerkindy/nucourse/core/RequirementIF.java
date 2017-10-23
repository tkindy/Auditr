package com.tylerkindy.nucourse.core;

import org.immutables.value.Value.Immutable;

@Immutable
@MyStyle
public interface RequirementIF extends RequirementCore {

  RemainingCoursesOperator getRemainingCoursesOperator();
}
