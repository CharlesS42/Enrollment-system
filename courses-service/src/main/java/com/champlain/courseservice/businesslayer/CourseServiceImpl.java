package com.champlain.courseservice.businesslayer;

import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.presentationlayer.CourseRequestModel;
import com.champlain.courseservice.presentationlayer.CourseResponseModel;
import com.champlain.courseservice.utils.EntityModelUtil;
import com.champlain.courseservice.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Flux<CourseResponseModel> getAllCourses() {
        return courseRepository.findAll()
                .map(EntityModelUtil::toCourseResponseModel);   // for each element in the flux (for each row in db), run give the course to the method
                                                                // '::' = method reference
    }

    @Override
    public Mono<CourseResponseModel> getCourseByCourseId(String courseId) {
        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Course id not found: " + courseId))))
                .doOnNext(c -> log.debug("The course response entity is: " + c.toString()))
                .map(EntityModelUtil::toCourseResponseModel);
    }

    @Override
    public Mono<CourseResponseModel> addCourse(Mono<CourseRequestModel> courseRequestModel) {
        return courseRequestModel
                .map(EntityModelUtil::toCourseEntity)
                .doOnNext(e -> e.setCourseId(EntityModelUtil.generateUUIDString()))
                .flatMap(courseRepository::save)
                .map(EntityModelUtil::toCourseResponseModel);
    }

    @Override
    public Mono<CourseResponseModel> updateCourseByCourseId(Mono<CourseRequestModel> courseRequestModel, String courseId) {
        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Course id not found: " + courseId))))
                .flatMap(found -> courseRequestModel
                        .map(EntityModelUtil::toCourseEntity)
                        .doOnNext(e -> e.setCourseId(found.getCourseId()))
                        .doOnNext(e -> e.setId(found.getId()))
                )
                .flatMap(courseRepository::save)
                .map(EntityModelUtil::toCourseResponseModel);
    }

    @Override
    public Mono<CourseResponseModel> deleteCourseByCourseId(String courseId) {
        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException("Course id not found: " + courseId))))
                .flatMap(found -> courseRepository.delete(found)
                        .then(Mono.just(found)))
                .map(EntityModelUtil::toCourseResponseModel);
    }
}
