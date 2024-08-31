package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.businesslayer.CourseService;
import com.champlain.courseservice.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("api/v1/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }


    @GetMapping(value="", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CourseResponseModel> getAllCourses() {
        return courseService.getAllCourses();
    }

    //todo: getCourseByCourseId
    @GetMapping(value = "/{courseId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<ResponseEntity<CourseResponseModel>> getCourseByCourseId(@PathVariable String courseId) {
        return Mono.just(courseId) // creates a publisher
                .filter(id -> id.length() == 36) // validate the course id
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided Course id is invalid: " + courseId)))
                .flatMap(courseService::getCourseByCourseId)
                // before: .map makes Mono<Mono<...>>, flatmap makes it Mono<CourseResponseModel>
                .map(ResponseEntity::ok);
    }

    //todo: addCourse
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CourseResponseModel>> addCourse(@RequestBody Mono<CourseRequestModel> courseRequestModel){
        return courseService.addCourse(courseRequestModel)
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c))
                .defaultIfEmpty(ResponseEntity.badRequest().build());

    }

    //todo: updateCourseByCourseId
    @PutMapping(value = "/{courseId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CourseResponseModel>> updateCourseByCourseId(@RequestBody Mono<CourseRequestModel> courseRequestModel, @PathVariable String courseId) {
        return Mono.just(courseId)
                .filter(id -> id.length() == 36) // validate the course id
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided Course id is invalid: " + courseId)))
                .flatMap(id-> courseService.updateCourseByCourseId(courseRequestModel,id))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    //todo: deleteCourseByCourseId
    @DeleteMapping(value = "/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CourseResponseModel>> deleteCourseByCourseId(@PathVariable String courseId) {
        return Mono.just(courseId)
                .filter(id -> id.length() == 36) // validate the course id
                .switchIfEmpty(Mono.error(new InvalidInputException("Provided Course id is invalid: " + courseId)))
                .flatMap(courseService::deleteCourseByCourseId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }
}
