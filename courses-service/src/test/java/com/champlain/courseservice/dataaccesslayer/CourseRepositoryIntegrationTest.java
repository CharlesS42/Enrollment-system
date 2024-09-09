package com.champlain.courseservice.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@ActiveProfiles("test")
class CourseRepositoryIntegrationTest {

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    public void setupDB() {
        StepVerifier
                .create(courseRepository.deleteAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenFindCourseByCourseID_withExistingId_thenReturnCourse() {
        // arrange
        String courseId = UUID.randomUUID().toString();
        Course course = Course.builder()
                .courseId(courseId)
                .courseNumber("cat-420")
                .courseName("Web Services")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();

        StepVerifier
                .create(courseRepository.save(course))
                .consumeNextWith(insertedCourse -> {
                    assertNotNull(insertedCourse);
                    assertEquals(course.getCourseId(), insertedCourse.getCourseId());
                })
                .verifyComplete();
        // act & assert
        StepVerifier
                .create(courseRepository.
                        findCourseByCourseId(courseId))
                .consumeNextWith(foundCourse -> {
                    assertNotNull(foundCourse);
                    assertEquals(course.getCourseId(), foundCourse.getCourseId());
                })
                .verifyComplete();
    }

    @Test
    void whenFindCourseByCourseID_withNonExistingId_thenReturnEmptyMono() {
        String nonExistingCourseId = UUID.randomUUID().toString();

        StepVerifier
                .create(courseRepository.findCourseByCourseId(nonExistingCourseId))
                .expectNextCount(0)
                .verifyComplete();
    }


}