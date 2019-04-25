package com.gaufoo.bbs.components.scutCourse;

import com.gaufoo.bbs.components.scutCourse.common.Course;
import com.gaufoo.bbs.components.scutCourse.common.CourseCode;

import java.util.Optional;
import java.util.stream.Stream;

public interface CourseFactory {
    Stream<Course> allCourses();

    CourseCode generateCourseCode(Course course);

    Optional<Course> getCourseFromCode(CourseCode courseCode);

    static CourseFactory defau1t() {
        return new CourseFactoryImpl();
    }
}
