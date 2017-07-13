package com.tylerkindy.auditr.db.repos;

import com.google.inject.Inject;
import com.tylerkindy.auditr.core.CatalogCourse;
import com.tylerkindy.auditr.core.CatalogCourseRequest;
import com.tylerkindy.auditr.db.daos.CatalogCourseDao;
import java.util.Collection;
import java.util.Optional;

public class CatalogCourseRepository {
  private final CatalogCourseDao dao;

  @Inject
  public CatalogCourseRepository(CatalogCourseDao dao) {
    this.dao = dao;
  }

  public CatalogCourse getById(int id) {
    return dao.getById(id);
  }

  public Optional<CatalogCourse> getBySubNum(String subject, int number) {
    return Optional.ofNullable(dao.getBySubNum(subject, number));
  }

  public Collection<CatalogCourse> getAll() {
    return dao.getAllCourses();
  }

  public CatalogCourse create(CatalogCourseRequest course) {
    int id = dao.create(course);
    return CatalogCourse.builder()
        .from(course)
        .setId(id)
        .build();
  }

  public CatalogCourse update(CatalogCourse course) {
    dao.update(course);
    return dao.getById(course.getId());
  }
}
