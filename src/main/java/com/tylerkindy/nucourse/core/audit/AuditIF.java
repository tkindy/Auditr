package com.tylerkindy.nucourse.core.audit;

import com.tylerkindy.nucourse.core.MyStyle;
import java.util.Collection;
import org.immutables.value.Value.Immutable;

@Immutable
@MyStyle
public interface AuditIF {

  Collection<RequirementGroup> getRequirementGroups();
}
