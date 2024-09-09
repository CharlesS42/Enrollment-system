package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.businesslayer.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = CourseController.class)
class CourseControllerUnitTest {

    //@Autowired
    //private CourseController courseController;

    @MockBean
    private CourseService courseService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void whenAddCourse_thenReturnCourseResponseModel() {
        // arrange
        CourseRequestModel courseRequestModel= CourseRequestModel.builder()
                .courseNumber("N52-LA")
                .courseName("final project 1")
                .numHours(45)
                .numCredits(3.0)
                .department("comp science")
                .build();

        String courseId = UUID.randomUUID().toString();
        CourseResponseModel courseResponseModel = CourseResponseModel.builder()
                .courseNumber("N52-LA")
                .courseName("final project 1")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();

        when(courseService.addCourse(any(Mono.class))).thenReturn(Mono.just(courseResponseModel));

        // act
        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(courseRequestModel), CourseRequestModel.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class)
                .isEqualTo(courseResponseModel);

        verify(courseService, times(1)).addCourse(any(Mono.class));
    }

    @Test
    public void whenGetAllCourse_thenReturnAllCourses() {
        // arrange
        when(courseService.getAllCourses()).thenReturn(Flux.just());

        // act
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

        verify(courseService, times(1)).addCourse(any(Mono.class));
    }

    @Test
    public void whenGetCourseByCourseId_thenReturnCourseResponseModel() {
        // arrange
        CourseRequestModel courseRequestModel= CourseRequestModel.builder()
                .courseNumber("N52-LA")
                .courseName("final project 1")
                .numHours(45)
                .numCredits(3.0)
                .department("comp science")
                .build();

        String courseId = UUID.randomUUID().toString();
        CourseResponseModel courseResponseModel = CourseResponseModel.builder()
                .courseNumber("N52-LA")
                .courseName("final project 1")
                .numHours(45)
                .numCredits(3.0)
                .department("Computer Science")
                .build();

        when(courseService.addCourse(any(Mono.class))).thenReturn(Mono.just(courseResponseModel));

        // act
        webTestClient
                .post()
                .uri("/api/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(courseRequestModel), CourseRequestModel.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class)
                .isEqualTo(courseResponseModel);

        verify(courseService, times(1)).addCourse(any(Mono.class));
    }

}
