package com.gaufoo.bbs.components.scutCourse;

import com.gaufoo.bbs.components.scutCourse.common.Course;
import com.gaufoo.bbs.components.scutCourse.common.CourseCode;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class CourseFactoryImpl implements CourseFactory {
    @Override
    public Stream<Course> allCourses() {
        return Arrays.stream(Course.values());
    }

    @Override
    public CourseCode generateCourseCode(Course course) {
        return CourseCode.of(String.format("%03d", course.ordinal()));
    }

    @Override
    public Optional<Course> getCourseFromCode(CourseCode code) {
        if (code.value.length() != 3) return Optional.empty();
        if (!code.value.matches("\\d*")) return Optional.empty();
        int index = Integer.parseInt(code.value);
        Course[] courses = Course.values();
        if (index >= courses.length) return Optional.empty();
        else return Optional.of(courses[index]);
    }
}
