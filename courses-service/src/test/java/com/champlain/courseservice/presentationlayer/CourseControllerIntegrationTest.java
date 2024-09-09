package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourseControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private CourseRepository courseRepository;

    private final Long dbSize = 1000L;

    @BeforeEach
    public void dbSetup() {
        // remove before pushing to production
        StepVerifier
                .create(courseRepository.count())
                .consumeNextWith(count -> {
                    assertEquals(dbSize, count);
                })
                .verifyComplete();
    }

    @Test
    public void whenAddCourse_thenReturnCourseResponseModel(){
        CourseRequestModel courseRequestModel= CourseRequestModel.builder()
                .courseNumber("N52-LA")
                .courseName("final project 1")
                .numHours(45)
                .numCredits(3.0)
                .department("comp science")
                .build();

        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestModel)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class)
                .value(courseResponseModel -> {
                    assertNotNull(courseResponseModel);
                    assertNotNull(courseResponseModel.getCourseId());
                    assertEquals(courseRequestModel.getCourseNumber(), courseResponseModel.getCourseNumber());
                    assertEquals(courseRequestModel.getCourseName(), courseResponseModel.getCourseName());
                    assertEquals(courseRequestModel.getNumHours(), courseResponseModel.getNumHours());
                    assertEquals(courseRequestModel.getNumCredits(), courseResponseModel.getNumCredits());
                    assertEquals(courseRequestModel.getDepartment(), courseResponseModel.getDepartment());
                });

        StepVerifier
                .create(courseRepository.count())
                .consumeNextWith(count->{
                    assertEquals(dbSize +1, count);
                })
                .verifyComplete();
    }


    @Test
    public void whenGetAllCourses_thenReturnAllCourses() {
        webTestClient
                .get()
                .uri("/api/v1/courses")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(CourseResponseModel.class)
                .value(courseResponseModels -> {
                    assertNotNull(courseResponseModels);
                    assertEquals(dbSize, courseResponseModels.size());
                });


    }

    @Test
    public void whenGetCourseByCourseId_thenReturnCourse() {
        String courseId = "275c1138-0190-426e-94d4-4aaeb838acac";

        webTestClient
                .get()
                .uri("/api/v1/courses/275c1138-0190-426e-94d4-4aaeb838acac")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class)
                .value(courseResponseModels -> {
                    assertNotNull(courseResponseModels);
                    assertEquals(courseId, courseResponseModels.getCourseId()); // used ChatGPT to suggest a proper assertEquals for this case
                });
    }

    @Test
    public void whenGetCourseByNotFoundCourseId_thenReturnNotFoundException() {
        String NON_EXISTING_COURSEID = "275c1138-0190-426e-94d4-4aaeb838a000";

        webTestClient
                .get()
                .uri("/api/v1/courses/275c1138-0190-426e-94d4-4aaeb838a000")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Course id not found: " + NON_EXISTING_COURSEID);
    }

    @Test
    public void whenUpdateCourseByCourseId_thenReturnCourseResponseModel() {
        String courseId = "275c1138-0190-426e-94d4-4aaeb838acac";

        CourseRequestModel courseRequestModel= CourseRequestModel.builder()
                .courseNumber("fak-000")
                .courseName("Java 2") // was "Java 1"
                .numHours(75)
                .numCredits(1.5)
                .department("Physics")
                .build();


        webTestClient
                .put()
                .uri("/api/v1/courses/{courseId}", courseId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestModel)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class)
                .value(courseResponseModel -> {
                    assertNotNull(courseResponseModel);
                    assertNotNull(courseResponseModel.getCourseId());
                    assertEquals(courseRequestModel.getCourseNumber(), courseResponseModel.getCourseNumber());
                    assertEquals(courseRequestModel.getCourseName(), courseResponseModel.getCourseName());
                    assertEquals(courseRequestModel.getNumHours(), courseResponseModel.getNumHours());
                    assertEquals(courseRequestModel.getNumCredits(), courseResponseModel.getNumCredits());
                    assertEquals(courseRequestModel.getDepartment(), courseResponseModel.getDepartment());
                });
    }

    @Test
    public void whenUpdateCourseByNotFoundCourseId_thenReturnNotFoundException() {
        String NON_EXISTING_COURSEID = "275c1138-0190-426e-94d4-4aaeb838a000";

        CourseRequestModel courseRequestModel= CourseRequestModel.builder()
                .courseNumber("fak-000")
                .courseName("Java 2") // was "Java 1"
                .numHours(75)
                .numCredits(1.5)
                .department("Physics")
                .build();


        webTestClient
                .put()
                .uri("/api/v1/courses/{courseId}", NON_EXISTING_COURSEID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestModel)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Course id not found: " + NON_EXISTING_COURSEID);
    }

    @Test
    public void whenDeleteCourseByCourseId_thenReturnCourseResponseModel() {
        String courseId = "275c1138-0190-426e-94d4-4aaeb838acac";

        webTestClient
                .delete()
                .uri("/api/v1/courses/{courseId}", courseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class)
                .value(courseResponseModels -> {
                    assertNotNull(courseResponseModels);
                    assertEquals(courseId, courseResponseModels.getCourseId());
                });

        StepVerifier
                .create(courseRepository.count())
                .consumeNextWith(count->{
                    assertEquals(dbSize -1, count);
                })
                .verifyComplete();

    }

    @Test
    public void whenDeleteCourseByNotFoundCourseId_thenReturnNotFoundException() {
        String NON_EXISTING_COURSEID = "275c1138-0190-426e-94d4-4aaeb838a000";

        webTestClient
                .delete()
                .uri("/api/v1/courses/{courseId}", NON_EXISTING_COURSEID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Course id not found: " + NON_EXISTING_COURSEID);

    }
}