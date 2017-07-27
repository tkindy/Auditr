package com.tylerkindy.nucourse.db.daos;

import com.hubspot.rosetta.jdbi.BindWithRosetta;
import com.tylerkindy.nucourse.core.CatalogCourse;
import com.tylerkindy.nucourse.core.CatalogCourseRequest;
import java.util.Collection;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface CatalogCourseDao {
  @SqlQuery("SELECT * FROM catalog_courses WHERE id = :id")
  CatalogCourse getById(@Bind("id") int id);

  @SqlQuery("SELECT * FROM catalog_courses WHERE subject = :subject AND number = :number")
  CatalogCourse getBySubNum(@Bind("subject") String subject, @Bind("number") int number);

  @SqlQuery("SELECT * FROM catalog_courses")
  Collection<CatalogCourse> getAllCourses();

  @GetGeneratedKeys
  @SqlUpdate("INSERT INTO catalog_courses (subject, number, name, description, credits) "
      + "VALUES (:subject, :number, :name, :description, :credits)")
  int create(@BindWithRosetta CatalogCourseRequest course);

  @SqlUpdate("UPDATE catalog_courses SET subject = :subject, number = :number, name = :name, "
      + "description = :description, credits = :credits WHERE id = :id")
  void update(@BindWithRosetta CatalogCourse course);
}
