package com.tylerkindy.nucourse.core;

import java.util.Collection;
import org.immutables.value.Value.Immutable;

@Immutable
@MyStyle
public interface RequirementGroupIF extends RequirementCore {

  Collection<Requirement> getRequirements();
}
