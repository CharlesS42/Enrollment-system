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

    String courseId = UUID.randomUUID().toString();

    CourseRequestModel courseRequestModel= CourseRequestModel.builder()
            .courseNumber("N52-LA")
            .courseName("final project 1")
            .numHours(45)
            .numCredits(3.0)
            .department("Computer Science")
            .build();

    CourseResponseModel courseResponseModel = CourseResponseModel.builder()
            .courseId(courseId)
            .courseNumber("N52-LA")
            .courseName("final project 1")
            .numHours(45)
            .numCredits(3.0)
            .department("Computer Science")
            .build();

    @Test
    public void whenAddCourse_thenReturnCourseResponseModel() {
        // arrange

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
                .value(crm -> {
                    assertNotNull(crm);
                    assertNotNull(crm.getCourseId());
                    assertEquals(courseRequestModel.getCourseNumber(), crm.getCourseNumber());
                    assertEquals(courseRequestModel.getCourseName(), crm.getCourseName());
                    assertEquals(courseRequestModel.getNumHours(), crm.getNumHours());
                    assertEquals(courseRequestModel.getNumCredits(), crm.getNumCredits());
                    assertEquals(courseRequestModel.getDepartment(), crm.getDepartment());
                })
                .isEqualTo(courseResponseModel);

        verify(courseService, times(1)).addCourse(any(Mono.class));
    }

    @Test
    public void whenGetAllCourse_thenReturnAllCourses() {
        // arrange
        when(courseService.getAllCourses()).thenReturn(Flux.just(courseResponseModel));

        // act
        webTestClient
                .get()
                .uri("/api/v1/courses")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(CourseResponseModel.class)
                .contains(courseResponseModel);

        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    public void whenGetCourseByCourseId_thenReturnCourseResponseModel() {
        // arrange
        when(courseService.getCourseByCourseId(courseId)).thenReturn(Mono.just(courseResponseModel));

        // act
        webTestClient
                .get()
                .uri("/api/v1/courses/" + courseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class)
                .isEqualTo(courseResponseModel);

        verify(courseService, times(1)).getCourseByCourseId(courseId);
    }

    @Test
    public void whenUpdateCourseByCourseId_thenReturnCourseResponseModel() {
        // arrange
        when(courseService.updateCourseByCourseId(any(Mono.class), eq(courseId))).thenReturn(Mono.just(courseResponseModel));

        // act
        webTestClient
                .put()
                .uri("/api/v1/courses/" + courseId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(courseRequestModel), CourseRequestModel.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class)
                .value(crm -> {
                    assertNotNull(crm);
                    assertNotNull(crm.getCourseId());
                    assertEquals(courseRequestModel.getCourseNumber(), crm.getCourseNumber());
                    assertEquals(courseRequestModel.getCourseName(), crm.getCourseName());
                    assertEquals(courseRequestModel.getNumHours(), crm.getNumHours());
                    assertEquals(courseRequestModel.getNumCredits(), crm.getNumCredits());
                    assertEquals(courseRequestModel.getDepartment(), crm.getDepartment());
                })
                .isEqualTo(courseResponseModel);

        // assert
        verify(courseService, times(1)).updateCourseByCourseId(any(Mono.class), eq(courseId));

    }

    @Test
    public void whenDeleteCourseByCourseId_thenReturnCourseResponseModel() {
        // arrange
        when(courseService.deleteCourseByCourseId(courseId)).thenReturn(Mono.just(courseResponseModel));

        // act
        webTestClient
                .delete()
                .uri("/api/v1/courses/{id}", courseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseModel.class)
                .isEqualTo(courseResponseModel);

        // assert
        verify(courseService, times(1)).deleteCourseByCourseId(courseId);

    }

    @Test
    public void whenGetCourseByInvalidCourseId_thenReturnIllegalArgumentException() {
        // arrange
        String wrongFormatCourseId = "random";

        when(courseService.getCourseByCourseId(wrongFormatCourseId)).thenThrow(IllegalArgumentException.class);

        // act & assert
        webTestClient
                .get()
                .uri("/api/v1/courses/{id}", wrongFormatCourseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(422);
    }
}
