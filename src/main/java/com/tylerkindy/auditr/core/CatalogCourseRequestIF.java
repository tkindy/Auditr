package com.tylerkindy.auditr.core;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

@Immutable
@MyStyle
@JsonSerialize
@JsonDeserialize
public interface CatalogCourseRequestIF extends CatalogCourseCore {

}
