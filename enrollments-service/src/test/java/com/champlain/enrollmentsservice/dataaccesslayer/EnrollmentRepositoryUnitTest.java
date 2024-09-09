package com.champlain.enrollmentsservice.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class EnrollmentRepositoryUnitTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private final String enrollmentId = "06a7d573-bcab-4db3-956f-773324b92a80";

    private final Enrollment enrollment1 = Enrollment.builder()
            .enrollmentId("06a7d573-bcab-4db3-956f-773324b92a80")
            .enrollmentYear(2021)
            .semester(Semester.FALL)
            .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
            .studentFirstName("Christine")
            .studentLastName("Gerard")
            .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
            .courseNumber("trs-075")
            .courseName("Web Services")
            .build();

    @BeforeEach
    public void setUp() {
        StepVerifier
                .create(enrollmentRepository.deleteAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findEnrollmentByEnrollmentId_withExistingId_thenReturnEnrollment() {
        StepVerifier
                .create(enrollmentRepository.save(enrollment1))
                .consumeNextWith(insertedEnrollment -> {
                    assertNotNull(insertedEnrollment);
                    assertEquals(enrollment1.getEnrollmentId(), insertedEnrollment.getEnrollmentId());
                    assertEquals(enrollment1.getEnrollmentYear(), insertedEnrollment.getEnrollmentYear());
                    assertEquals(enrollment1.getSemester(), insertedEnrollment.getSemester());
                    assertEquals(enrollment1.getStudentId(), insertedEnrollment.getStudentId());
                    assertEquals(enrollment1.getStudentFirstName(), insertedEnrollment.getStudentFirstName());
                    assertEquals(enrollment1.getStudentLastName(), insertedEnrollment.getStudentLastName());
                    assertEquals(enrollment1.getCourseId(), insertedEnrollment.getCourseId());
                    assertEquals(enrollment1.getCourseNumber(), insertedEnrollment.getCourseNumber());
                    assertEquals(enrollment1.getCourseName(), insertedEnrollment.getCourseName());
                })
                .verifyComplete();

        // act & assert
        StepVerifier
                .create(enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId))
                .consumeNextWith(foundEnrollment -> {
                    assertNotNull(foundEnrollment);
                    assertEquals(enrollment1.getEnrollmentId(), foundEnrollment.getEnrollmentId());
                    assertEquals(enrollment1.getEnrollmentYear(), foundEnrollment.getEnrollmentYear());
                    assertEquals(enrollment1.getSemester(), foundEnrollment.getSemester());
                    assertEquals(enrollment1.getStudentId(), foundEnrollment.getStudentId());
                    assertEquals(enrollment1.getStudentFirstName(), foundEnrollment.getStudentFirstName());
                    assertEquals(enrollment1.getStudentLastName(), foundEnrollment.getStudentLastName());
                    assertEquals(enrollment1.getCourseId(), foundEnrollment.getCourseId());
                    assertEquals(enrollment1.getCourseNumber(), foundEnrollment.getCourseNumber());
                    assertEquals(enrollment1.getCourseName(), foundEnrollment.getCourseName());
                })
                .verifyComplete();
    }

    @Test
    void whenFindEnrollmentByEnrollmentId_withNonExistingId_thenReturnEmptyResponseModel() {
        // arrange
        String nonExistingEnrollmentId = "06a7d573-bcab-4db3-956f-773324b92a81";

        // act & assert
        StepVerifier
                .create(enrollmentRepository.findEnrollmentByEnrollmentId(nonExistingEnrollmentId))
                .expectNextCount(0)
                .verifyComplete();
    }

}