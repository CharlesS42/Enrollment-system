package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.dataaccesslayer.Semester;

import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import com.champlain.enrollmentsservice.utils.exceptions.NotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class EnrollmentServiceUnitTest {

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Mock
    private EnrollmentRepository enrollmentRepository;

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

    private final Enrollment enrollment2 = Enrollment.builder()
            .enrollmentId("98f7b33a-d62a-420a-a84a-05a27c85fc91")
            .enrollmentYear(2021)
            .semester(Semester.FALL)
            .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
            .studentFirstName("Christine")
            .studentLastName("Gerard")
            .courseId("d819e4f4-25af-4d33-91e9-2c45f0071606")
            .courseNumber("ygo-675")
            .courseName("Shakespeare's Greatest Works")
            .build();

    //UUID for non-existent enrollment
    private final String nonExistentEnrollmentId = "5a8b09ff-05ee-43dd-abdf-6a3ec3833edd";



    @Test
    public void whenGetAllEnrollments_thenReturnEnrollmentsAsFlux() {
        // Arrange
        when(enrollmentRepository.findAll()).thenReturn(Flux.just(enrollment1, enrollment2));

        // Act
        Flux<EnrollmentResponseModel> result = enrollmentService.getAllEnrollments();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(enrollment -> enrollment.getEnrollmentId().equals(enrollment1.getEnrollmentId()))
                .expectNextMatches(enrollment -> enrollment.getEnrollmentId().equals(enrollment2.getEnrollmentId()))
                .verifyComplete();
    }

    @Test
    public void whenGetEnrollmentById_thenReturnEnrollmentAsMono() {
        // Arrange
        when(enrollmentRepository.findEnrollmentByEnrollmentId(enrollment1.getEnrollmentId())).thenReturn(Mono.just(enrollment1));

        // Act
        Mono<EnrollmentResponseModel> result = enrollmentService.getEnrollmentByEnrollmentId(enrollment1.getEnrollmentId());

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(enrollment -> enrollment.getEnrollmentId().equals(enrollment1.getEnrollmentId()))
                .verifyComplete();
    }

    @Test
    public void whenGetEnrollmentById_thenEnrollmentIdDoesNotExist_thenThrowNotFoundException() {
        // Arrange
        String nonExistentEnrollmentId = "non-existent-id";
        when(enrollmentRepository.findEnrollmentByEnrollmentId(nonExistentEnrollmentId)).thenReturn(Mono.empty());

        // Act
        Mono<EnrollmentResponseModel> result = enrollmentService.getEnrollmentByEnrollmentId(nonExistentEnrollmentId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Enrollment id not found: " + nonExistentEnrollmentId))
                .verify();
    }



    @Test
    public void whenDeleteEnrollment_thenReturnEnrollmentResponseModel() {
        // Arrange
        when(enrollmentRepository.findEnrollmentByEnrollmentId(enrollment1.getEnrollmentId())).thenReturn(Mono.just(enrollment1));
        when(enrollmentRepository.delete(enrollment1)).thenReturn(Mono.empty());
        // Act
        Mono<EnrollmentResponseModel> result = enrollmentService.deleteEnrollmentByEnrollmentId(enrollment1.getEnrollmentId());
        // Assert
        StepVerifier
                .create(result)
                .expectNextMatches(enrollmentResponseModel->enrollmentResponseModel
                        .getEnrollmentId().equals(enrollment1.getEnrollmentId()))
                .verifyComplete();
    }

    @Test
    public void whenUpdateEnrollment_thenEnrollmentIdIsNotFound_thenReturnNotFoundException(){
        EnrollmentRequestModel updatedCourseEnrollmentModel = new EnrollmentRequestModel();

        when(enrollmentRepository.findEnrollmentByEnrollmentId(nonExistentEnrollmentId)).thenReturn(Mono.empty());

        // Act
        Mono<EnrollmentResponseModel> result = enrollmentService.updateEnrollmentByEnrollmentId(Mono.just(updatedCourseEnrollmentModel), nonExistentEnrollmentId);

        // Assert
        StepVerifier
                .create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Enrollment id not found: " + nonExistentEnrollmentId)
                )
                .verify();
    }

    @Test
    public void whenDeleteEnrollment_thenEnrollmentIdIsNotFound_thenThrowNotFoundException() {
        // Arrange
        when(enrollmentRepository.findEnrollmentByEnrollmentId(nonExistentEnrollmentId)).thenReturn(Mono.empty());

        // Act
        Mono<EnrollmentResponseModel> result = enrollmentService.deleteEnrollmentByEnrollmentId(nonExistentEnrollmentId);

        // Assert
        StepVerifier
                .create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals("Enrollment id not found: " + nonExistentEnrollmentId)
                )
                .verify();
    }


}