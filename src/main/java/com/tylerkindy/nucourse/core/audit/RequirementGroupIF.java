package com.tylerkindy.nucourse.core.audit;

import com.tylerkindy.nucourse.core.MyStyle;
import java.util.Collection;
import org.immutables.value.Value.Immutable;

@Immutable
@MyStyle
public interface RequirementGroupIF extends RequirementCore {

  Collection<Requirement> getRequirements();
}
