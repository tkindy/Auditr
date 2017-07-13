package com.tylerkindy.auditr.resources;

import com.google.inject.Inject;
import com.tylerkindy.auditr.core.CatalogCourse;
import com.tylerkindy.auditr.db.repos.CatalogCourseRepository;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("catalog")
public class CatalogCourseResource {
  private final CatalogCourseRepository catalogCourseRepository;

  @Inject
  public CatalogCourseResource(CatalogCourseRepository catalogCourseRepository) {
    this.catalogCourseRepository = catalogCourseRepository;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<CatalogCourse> getAllCourses() {
    return catalogCourseRepository.getAll();
  }
}
