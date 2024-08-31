package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.businesslayer.enrollments.EnrollmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/api/v1/enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping(value="", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EnrollmentResponseModel> getAllEnrollments() { return enrollmentService.getAllEnrollments(); }

    @PostMapping(value = "", produces = "application/json", consumes = "application/json")
    public Mono<ResponseEntity<EnrollmentResponseModel>> addEnrollment(@RequestBody Mono<EnrollmentRequestModel> enrollmentRequestModelMono){
        return enrollmentService.addEnrollment(enrollmentRequestModelMono)
                .map(e-> ResponseEntity.status(HttpStatus.CREATED).body(e))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
