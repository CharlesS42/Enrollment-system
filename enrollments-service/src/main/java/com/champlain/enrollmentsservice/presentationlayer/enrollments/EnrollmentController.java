package com.champlain.enrollmentsservice.presentationlayer.enrollments;

import com.champlain.enrollmentsservice.businesslayer.enrollments.EnrollmentService;
import com.champlain.enrollmentsservice.utils.exceptions.InvalidInputException;
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

    @GetMapping(value = "/{enrollmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EnrollmentResponseModel>> getEnrollmentByEnrollmentId(@PathVariable String enrollmentId) {
        return Mono.just(enrollmentId)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided Enrollment Id is invalid: " + enrollmentId)))
                .flatMap(enrollmentService::getEnrollmentByEnrollmentId)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "", produces = "application/json", consumes = "application/json")
    public Mono<ResponseEntity<EnrollmentResponseModel>> addEnrollment(@RequestBody Mono<EnrollmentRequestModel> enrollmentRequestModelMono){
        return enrollmentService.addEnrollment(enrollmentRequestModelMono)
                .map(e-> ResponseEntity.status(HttpStatus.CREATED).body(e))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping(value = "/{enrollmentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EnrollmentResponseModel>> updateEnrollmentByEnrollmentId(@RequestBody Mono<EnrollmentRequestModel> enrollmentRequestModel, @PathVariable String enrollmentId) {
        return Mono.just(enrollmentId)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided Enrollment id is invalid: " + enrollmentId)))
                .flatMap(id -> enrollmentService.updateEnrollmentByEnrollmentId(enrollmentRequestModel, enrollmentId))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping(value = "/{enrollmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<EnrollmentResponseModel>> deleteEnrollmentByEnrollmentId(@PathVariable String enrollmentId) {
        return Mono.just(enrollmentId)
                .filter(id -> id.length() == 36)
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided Enrollment id is invalid: " + enrollmentId)))
                .flatMap(enrollmentService::deleteEnrollmentByEnrollmentId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
