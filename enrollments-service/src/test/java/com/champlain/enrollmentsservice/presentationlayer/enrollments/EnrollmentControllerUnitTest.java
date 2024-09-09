package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.businesslayer.enrollments.EnrollmentService;
import com.champlain.enrollmentsservice.dataaccesslayer.Semester;
import com.champlain.enrollmentsservice.utils.exceptions.NotFoundException;
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

@WebFluxTest(controllers = EnrollmentController.class)
class EnrollmentControllerUnitTest {
    @MockBean
    private EnrollmentService enrollmentService;

    @Autowired
    private WebTestClient webTestClient;


    @Test
    public void whenAddEnrollment_thenReturnCreatedEnrollmentResponseModel() {
        // Arrange
        EnrollmentRequestModel enrollmentRequestModel = EnrollmentRequestModel.builder()
                .enrollmentYear(2023)
                .semester(Semester.FALL)
                .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                .build();

        EnrollmentResponseModel enrollmentResponseModel = EnrollmentResponseModel.builder()
                .enrollmentId(UUID.randomUUID().toString())
                .enrollmentYear(2021)
                .semester(Semester.FALL)
                .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .studentFirstName("Christine")
                .studentLastName("Gerard")
                .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                .courseNumber("trs-075")
                .courseName("Web Services")
                .build();

        when(enrollmentService.addEnrollment(any(Mono.class))).thenReturn(Mono.just(enrollmentResponseModel));

        // Act & Assert
        webTestClient
                .post()
                .uri("/api/v1/enrollment")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(enrollmentRequestModel), EnrollmentRequestModel.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .isEqualTo(enrollmentResponseModel);

        verify(enrollmentService, times(1)).addEnrollment(any(Mono.class));
    }

    @Test
    public void whenGetAllEnrollments_thenReturnListOfEnrollmentResponseModels() {
        // Arrange
        EnrollmentResponseModel enrollmentResponseModel1 = EnrollmentResponseModel.builder()
                .enrollmentId(UUID.randomUUID().toString())
                .enrollmentYear(2021)
                .semester(Semester.FALL)
                .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .studentFirstName("Christine")
                .studentLastName("Gerard")
                .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                .courseNumber("trs-075")
                .courseName("Web Services")
                .build();

        when(enrollmentService.getAllEnrollments()).thenReturn(Flux.just(enrollmentResponseModel1));

        // Act & Assert
        webTestClient
                .get()
                .uri("/api/v1/enrollment")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBodyList(EnrollmentResponseModel.class)
                .hasSize(1)
                .contains(enrollmentResponseModel1);

        verify(enrollmentService, times(1)).getAllEnrollments();
    }

    @Test
    public void whenGetEnrollmentById_thenReturnEnrollmentResponseModel() {
        // Arrange
        String enrollmentId = UUID.randomUUID().toString();

        EnrollmentResponseModel enrollmentResponseModel1 = EnrollmentResponseModel.builder()
                .enrollmentId(enrollmentId)
                .enrollmentYear(2021)
                .semester(Semester.FALL)
                .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .studentFirstName("Christine")
                .studentLastName("Gerard")
                .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                .courseNumber("trs-075")
                .courseName("Web Services")
                .build();

        when(enrollmentService.getEnrollmentByEnrollmentId(enrollmentId)).thenReturn(Mono.just(enrollmentResponseModel1));

        // Act & Assert
        webTestClient
                .get()
                .uri("/api/v1/enrollment/{id}", enrollmentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .isEqualTo(enrollmentResponseModel1);

        verify(enrollmentService, times(1)).getEnrollmentByEnrollmentId(enrollmentId);
    }

    @Test
    public void whenEnrollmentIdDoesNotExist_thenReturnNotFoundStatus() {
        // Arrange
        String nonExistentEnrollmentId = UUID.randomUUID().toString();

        when(enrollmentService.getEnrollmentByEnrollmentId(nonExistentEnrollmentId))
                .thenReturn(Mono.error(new NotFoundException("Enrollment id not found: " + nonExistentEnrollmentId)));

        // Act & Assert
        webTestClient
                .get()
                .uri("/api/v1/enrollment/{id}", nonExistentEnrollmentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Enrollment id not found: " + nonExistentEnrollmentId);

        verify(enrollmentService, times(1)).getEnrollmentByEnrollmentId(nonExistentEnrollmentId);
    }

    @Test
    public void whenUpdateEnrollment_thenReturnUpdatedEnrollmentResponseModel() {
        // Arrange
        String enrollmentId = UUID.randomUUID().toString();

        EnrollmentRequestModel enrollmentRequestModel = EnrollmentRequestModel.builder()
                .enrollmentYear(2023)
                .semester(Semester.FALL)
                .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                .build();

        EnrollmentResponseModel enrollmentResponseModel = EnrollmentResponseModel.builder()
                .enrollmentId(enrollmentId)
                .enrollmentYear(2021)
                .semester(Semester.FALL)
                .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .studentFirstName("Christine")
                .studentLastName("Gerard")
                .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                .courseNumber("trs-075")
                .courseName("Web Services")
                .build();

        when(enrollmentService.updateEnrollmentByEnrollmentId(any(Mono.class), anyString())).thenReturn(Mono.just(enrollmentResponseModel));

        // Act & Assert
        webTestClient
                .put()
                .uri("/api/v1/enrollment/{id}", enrollmentId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(enrollmentRequestModel), EnrollmentRequestModel.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .isEqualTo(enrollmentResponseModel);

        verify(enrollmentService, times(1)).updateEnrollmentByEnrollmentId(any(Mono.class), eq(enrollmentId));
    }

    @Test
    public void whenDeleteEnrollment_thenReturnDeletedEnrollmentResponseModel() {
        // Arrange
        String enrollmentId = UUID.randomUUID().toString();

        EnrollmentResponseModel enrollmentResponseModel = EnrollmentResponseModel.builder()
                .enrollmentId(enrollmentId)
                .enrollmentYear(2021)
                .semester(Semester.FALL)
                .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .studentFirstName("Christine")
                .studentLastName("Gerard")
                .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                .courseNumber("trs-075")
                .courseName("Web Services")
                .build();

        when(enrollmentService.deleteEnrollmentByEnrollmentId(enrollmentId)).thenReturn(Mono.just(enrollmentResponseModel));

        // Act & Assert
        webTestClient
                .delete()
                .uri("/api/v1/enrollment/{id}", enrollmentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .isEqualTo(enrollmentResponseModel);

        verify(enrollmentService, times(1)).deleteEnrollmentByEnrollmentId(enrollmentId);
    }

}