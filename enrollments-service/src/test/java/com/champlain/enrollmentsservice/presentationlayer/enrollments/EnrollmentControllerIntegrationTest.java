package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.MockServerConfigCoursesService;
import com.champlain.enrollmentsservice.MockServerConfigStudentsService;
import com.champlain.enrollmentsservice.businesslayer.enrollments.EnrollmentService;
import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.dataaccesslayer.Semester;
import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseResponseModel;
import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentResponseModel;
import org.junit.jupiter.api.*;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.Flow;

import static com.champlain.enrollmentsservice.MockServerConfigStudentsService.NON_EXISTING_STUDENTID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port = 0"})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EnrollmentControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private MockServerConfigStudentsService mockServerConfigStudentsService;
    private MockServerConfigCoursesService mockServerConfigCoursesService;

    private Enrollment enrollment1 = Enrollment.builder()
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

    Enrollment enrollment2 = Enrollment.builder()
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

    private StudentResponseModel studentResponseModel = StudentResponseModel.builder()
            .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
            .firstName("Christine")
            .lastName("Gerard")
            .program("Computer Science")
            .stuff("stuff")
            .build();

    private CourseResponseModel courseResponseModel = CourseResponseModel.builder()
            .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
            .courseNumber("trs-075")
            .courseName("Web Services")
            .department("Computer Science")
            .numHours(45)
            .numCredits(3.0)
            .build();

    private EnrollmentRequestModel enrollmentRequestModel = EnrollmentRequestModel.builder()
            .enrollmentYear(2021)
            .semester(Semester.FALL)
            .studentId(studentResponseModel.getStudentId())
            .courseId(courseResponseModel.getCourseId())
            .build();

    private EnrollmentRequestModel enrollment_withNonExistingStudentId_RequestModel = EnrollmentRequestModel.builder()
            .enrollmentYear(2021)
            .semester(Semester.FALL)
            .studentId(NON_EXISTING_STUDENTID)
            .courseId(courseResponseModel.getCourseId())
            .build();

    @BeforeAll
    public void startServers() {
        mockServerConfigStudentsService = new MockServerConfigStudentsService();
        mockServerConfigStudentsService.registerGetStudent1ByStudentIdEndpoint();

        mockServerConfigCoursesService = new MockServerConfigCoursesService();
        mockServerConfigCoursesService.registerGetCourse1ByCourseIdEndpoint();
    }

    @AfterAll
    public void stopServers() {
        mockServerConfigStudentsService.stopServer();
        mockServerConfigCoursesService.stopServer();
    }

    @BeforeEach
    public void setup() {
        Publisher<Enrollment> setupDB = enrollmentRepository.deleteAll()
                .thenMany(Flux.just(enrollment1, enrollment2))
                .flatMap(enrollmentRepository::save);

        StepVerifier
                .create(setupDB)
                .expectNextCount(2)
                .verifyComplete();
    }

    // with the help of GitHub Copilot, the basis was generated
    @Test
    public void whenGetAllEnrollments_thenReturnAllEnrollmentResponseModels() {
        // act
        webTestClient
                .get()
                .uri("/api/v1/enrollment")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(EnrollmentResponseModel.class)
                .value(enrollmentResponseModels -> {
                    assertEquals(2, enrollmentResponseModels.size());
                    assertEquals(enrollment1.getEnrollmentId(), enrollmentResponseModels.get(0).getEnrollmentId());
                    assertEquals(enrollment1.getEnrollmentYear(), enrollmentResponseModels.get(0).getEnrollmentYear());
                    assertEquals(enrollment1.getSemester(), enrollmentResponseModels.get(0).getSemester());
                    assertEquals(enrollment1.getStudentId(), enrollmentResponseModels.get(0).getStudentId());
                    assertEquals(enrollment1.getCourseId(), enrollmentResponseModels.get(0).getCourseId());
                    assertEquals(enrollment1.getStudentFirstName(), enrollmentResponseModels.get(0).getStudentFirstName());
                    assertEquals(enrollment1.getStudentLastName(), enrollmentResponseModels.get(0).getStudentLastName());
                    assertEquals(enrollment1.getCourseNumber(), enrollmentResponseModels.get(0).getCourseNumber());
                    assertEquals(enrollment1.getCourseName(), enrollmentResponseModels.get(0).getCourseName());

                    assertEquals(enrollment2.getEnrollmentId(), enrollmentResponseModels.get(1).getEnrollmentId());
                    assertEquals(enrollment2.getEnrollmentYear(), enrollmentResponseModels.get(1).getEnrollmentYear());
                    assertEquals(enrollment2.getSemester(), enrollmentResponseModels.get(1).getSemester());
                    assertEquals(enrollment2.getStudentId(), enrollmentResponseModels.get(1).getStudentId());
                    assertEquals(enrollment2.getCourseId(), enrollmentResponseModels.get(1).getCourseId());
                    assertEquals(enrollment2.getStudentFirstName(), enrollmentResponseModels.get(1).getStudentFirstName());
                    assertEquals(enrollment2.getStudentLastName(), enrollmentResponseModels.get(1).getStudentLastName());
                    assertEquals(enrollment2.getCourseNumber(), enrollmentResponseModels.get(1).getCourseNumber());
                    assertEquals(enrollment2.getCourseName(), enrollmentResponseModels.get(1).getCourseName());
                });

        // assert
        StepVerifier
                .create(enrollmentRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    // with the help of GitHub Copilot, the basis was generated
    @Test
    public void whenGetEnrollmentByEnrollmentId_thenReturnEnrollmentResponseModel() {
        // act
        webTestClient
                .get()
                .uri("/api/v1/enrollment/" + enrollment1.getEnrollmentId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .value(enrollmentResponseModel -> {
                    assertEquals(enrollment1.getEnrollmentId(), enrollmentResponseModel.getEnrollmentId());
                    assertEquals(enrollment1.getEnrollmentYear(), enrollmentResponseModel.getEnrollmentYear());
                    assertEquals(enrollment1.getSemester(), enrollmentResponseModel.getSemester());
                    assertEquals(enrollment1.getStudentId(), enrollmentResponseModel.getStudentId());
                    assertEquals(enrollment1.getCourseId(), enrollmentResponseModel.getCourseId());
                    assertEquals(enrollment1.getStudentFirstName(), enrollmentResponseModel.getStudentFirstName());
                    assertEquals(enrollment1.getStudentLastName(), enrollmentResponseModel.getStudentLastName());
                    assertEquals(enrollment1.getCourseNumber(), enrollmentResponseModel.getCourseNumber());
                    assertEquals(enrollment1.getCourseName(), enrollmentResponseModel.getCourseName());
                });

        // assert
        StepVerifier
                .create(enrollmentRepository.findEnrollmentByEnrollmentId(enrollment1.getEnrollmentId()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void whenAddEnrollment_thenReturnEnrollmentResponseModel(){
        webTestClient.post()
                .uri("/api/v1/enrollment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(enrollmentRequestModel), EnrollmentRequestModel.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .value(enrollmentResponseModel -> {
                    assertNotNull(enrollmentResponseModel.getEnrollmentId());
                    assertEquals(enrollmentRequestModel.getEnrollmentYear(), enrollmentResponseModel.getEnrollmentYear());
                    assertEquals(enrollmentRequestModel.getSemester(), enrollmentResponseModel.getSemester());
                    assertEquals(enrollmentRequestModel.getStudentId(), enrollmentResponseModel.getStudentId());
                    assertEquals(enrollmentRequestModel.getCourseId(), enrollmentResponseModel.getCourseId());
                    assertEquals(studentResponseModel.getFirstName(), enrollmentResponseModel.getStudentFirstName());
                    assertEquals(studentResponseModel.getLastName(), enrollmentResponseModel.getStudentLastName());
                    assertEquals(courseResponseModel.getCourseNumber(), enrollmentResponseModel.getCourseNumber());
                    assertEquals(courseResponseModel.getCourseName(), enrollmentResponseModel.getCourseName());
                });

        //assert
        StepVerifier
                .create(enrollmentRepository.findAll())
                .expectNextCount(3)
                .verifyComplete();
    }

    // with the help of GitHub Copilot, the basis was generated
    @Test
    public void whenUpdateEnrollment_thenReturnEnrollmentResponseModel(){
        //act
        webTestClient.put()
                .uri("/api/v1/enrollment/" + enrollment1.getEnrollmentId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(enrollmentRequestModel), EnrollmentRequestModel.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .value(enrollmentResponseModel -> {
                    assertEquals(enrollment1.getEnrollmentId(), enrollmentResponseModel.getEnrollmentId());
                    assertEquals(enrollmentRequestModel.getEnrollmentYear(), enrollmentResponseModel.getEnrollmentYear());
                    assertEquals(enrollmentRequestModel.getSemester(), enrollmentResponseModel.getSemester());
                    assertEquals(enrollmentRequestModel.getStudentId(), enrollmentResponseModel.getStudentId());
                    assertEquals(studentResponseModel.getFirstName(), enrollmentResponseModel.getStudentFirstName());
                    assertEquals(studentResponseModel.getLastName(), enrollmentResponseModel.getStudentLastName());
                    assertEquals(enrollmentRequestModel.getCourseId(), enrollmentResponseModel.getCourseId());
                    assertEquals(courseResponseModel.getCourseNumber(), enrollmentResponseModel.getCourseNumber());
                    assertEquals(courseResponseModel.getCourseName(), enrollmentResponseModel.getCourseName());
                });

        //assert
        StepVerifier
                .create(enrollmentRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void whenDeleteEnrollment_thenReturnEnrollmentResponseModel(){
        //act
        webTestClient.delete()
                .uri("/api/v1/enrollment/" + enrollment1.getEnrollmentId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseModel.class)
                .value(enrollmentResponseModel -> {
                    assertEquals(enrollment1.getEnrollmentId(), enrollmentResponseModel.getEnrollmentId());
                    assertEquals(enrollmentRequestModel.getEnrollmentYear(), enrollmentResponseModel.getEnrollmentYear());
                    assertEquals(enrollmentRequestModel.getSemester(), enrollmentResponseModel.getSemester());
                    assertEquals(enrollmentRequestModel.getStudentId(), enrollmentResponseModel.getStudentId());
                    assertEquals(studentResponseModel.getFirstName(), enrollmentResponseModel.getStudentFirstName());
                    assertEquals(studentResponseModel.getLastName(), enrollmentResponseModel.getStudentLastName());
                    assertEquals(enrollmentRequestModel.getCourseId(), enrollmentResponseModel.getCourseId());
                    assertEquals(courseResponseModel.getCourseNumber(), enrollmentResponseModel.getCourseNumber());
                    assertEquals(courseResponseModel.getCourseName(), enrollmentResponseModel.getCourseName());
                });

        //assert
        StepVerifier
                .create(enrollmentRepository.findAll())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void whenAddEnrollment_withNonExistingStudentId_thenThrowNotFoundException() {

        // act
        webTestClient.post()
                .uri("/api/v1/enrollment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(enrollment_withNonExistingStudentId_RequestModel), EnrollmentRequestModel.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("StudentId not found: " + NON_EXISTING_STUDENTID);

        // assert
        StepVerifier
                .create(enrollmentRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }
}