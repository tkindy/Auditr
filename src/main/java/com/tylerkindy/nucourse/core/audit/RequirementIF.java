package com.tylerkindy.nucourse.core.audit;

import com.tylerkindy.nucourse.core.MyStyle;
import org.immutables.value.Value.Immutable;

@Immutable
@MyStyle
public interface RequirementIF extends RequirementCore {

  RemainingCoursesOperator getRemainingCoursesOperator();
}
