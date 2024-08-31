package com.champlain.enrollmentsservice.businesslayer.enrollments;

import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseClient;
import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentClientAsynchronous;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentRequestModel;
import com.champlain.enrollmentsservice.presentationlayer.enrollments.EnrollmentResponseModel;
import com.champlain.enrollmentsservice.utils.exceptions.EntityModelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseClient courseClient;
    private final StudentClientAsynchronous studentClient;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository, CourseClient courseClient, StudentClientAsynchronous studentClient) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseClient = courseClient;
        this.studentClient = studentClient;
    }

    @Override
    public Flux<EnrollmentResponseModel> getAllEnrollments() {
        return enrollmentRepository.findAll()
                .map(EntityModelUtil::toEnrollmentResponseModel);
    }

    @Override
    public Mono<EnrollmentResponseModel> addEnrollment(Mono<EnrollmentRequestModel> enrollmentRequestModel) {
        return enrollmentRequestModel
                .map(RequestContext::new)
                .flatMap(this::studentRequestResponse)
                .flatMap(this::courseRequestResponse)
                .map(EntityModelUtil::toEnrollmentEntity)
                .flatMap(enrollmentRepository::save)
                .map(EntityModelUtil::toEnrollmentResponseModel);
    }

    private Mono<RequestContext> studentRequestResponse(RequestContext rc) {
        return studentClient
                .getStudentByStudentId(rc.getEnrollmentRequestModel().getStudentId())
                .doOnNext(rc::setStudentResponseModel)
                .thenReturn(rc);
    }

    private Mono<RequestContext> courseRequestResponse(RequestContext rc) {
        return courseClient
                .getCourseByCourseId(rc.getEnrollmentRequestModel().getCourseId())
                .doOnNext(rc::setCourseResponseModel)
                .thenReturn(rc);
    }
}
