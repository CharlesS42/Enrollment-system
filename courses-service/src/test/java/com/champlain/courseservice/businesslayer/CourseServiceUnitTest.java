package com.champlain.courseservice.businesslayer;

import com.champlain.courseservice.dataaccesslayer.Course;
import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.presentationlayer.CourseRequestModel;
import com.champlain.courseservice.presentationlayer.CourseResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceUnitTest {

    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CourseRepository courseRepository;
    Course course1 = Course.builder()
            .id(1)
            .courseId(UUID.randomUUID().toString())
            .courseNumber("cat-420")
            .courseName("Web Service")
            .numHours(45)
            .numCredits(3.0)
            .department("computer science")
            .build();

    Course course2 = Course.builder()
            .id(2)
            .courseId(UUID.randomUUID().toString())
            .courseNumber("cat-421")
            .courseName("Web Service")
            .numHours(45)
            .numCredits(3.0)
            .department("computer science")
            .build();

    Course course3 = Course.builder()
            .id(3)
            .courseId(UUID.randomUUID().toString())
            .courseNumber("cat-422")
            .courseName("Web Service")
            .numHours(45)
            .numCredits(3.0)
            .department("computer science")
            .build();

    @Test
    public void whenGetAllCourses_thenReturnThreeCourses() {
        // arrange
        when(courseRepository.findAll())
                .thenReturn(Flux.just(course1, course2, course3));

        // act
        Flux<CourseResponseModel> result = courseService.getAllCourses();

        // assert
        StepVerifier
                .create(result)
                .expectNextMatches(courseResponseModel -> courseResponseModel.getCourseId().equals(course1.getCourseId()))
                .expectNextMatches(courseResponseModel -> courseResponseModel.getCourseId().equals(course2.getCourseId()))
                .expectNextMatches(courseResponseModel -> courseResponseModel.getCourseId().equals(course3.getCourseId()))
                .verifyComplete();

    }

    @Test
    public void whenGetCourseByCourseId_thenReturnCourseResponseModel() {
        // arrange
        when(courseRepository.findCourseByCourseId(course1.getCourseId()))
                .thenReturn(Mono.just(course1));

        // act
        Mono<CourseResponseModel> result = courseService.getCourseByCourseId(course1.getCourseId());

        // assert
        StepVerifier
                .create(result)
                .expectNextMatches(courseResponseModel -> courseResponseModel.getCourseId().equals(course1.getCourseId()))
                .verifyComplete();

    }

    @Test
    public void whenAddCourse_thenReturnCourseResponseModel() {
        // arrange
        when(courseRepository.save(any(Course.class)))
                .thenReturn(Mono.just(course1));

        CourseRequestModel courseRequestModel = new CourseRequestModel();
        BeanUtils.copyProperties(course1, courseRequestModel);

        // act
        Mono<CourseResponseModel> result = courseService.addCourse(Mono.just(courseRequestModel));

        // assert
        StepVerifier
                .create(result)
                .expectNextMatches(courseResponseModel -> {
                    assertNotNull(courseResponseModel);
                    assertEquals(courseResponseModel.getCourseId(), course1.getCourseId());
                    assertEquals(courseResponseModel.getCourseNumber(), course1.getCourseNumber());
                    assertEquals(courseResponseModel.getCourseName(), course1.getCourseName());
                    assertEquals(courseResponseModel.getNumHours(), course1.getNumHours());
                    assertEquals(courseResponseModel.getNumCredits(), course1.getNumCredits());
                    assertEquals(courseResponseModel.getDepartment(), course1.getDepartment());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void whenUpdateCourse_thenReturnCourseResponseModel() {
        // arrange
        when(courseRepository.findCourseByCourseId(course1.getCourseId()))
                .thenReturn(Mono.just(course1));

        when(courseRepository.save(any(Course.class)))
                .thenReturn(Mono.just(course1));

        CourseRequestModel newCourseRequestModel = new CourseRequestModel("cat-420", "Web Service", 45, 3.0, "computer science");

        // act
        Mono<CourseResponseModel> result = courseService.updateCourseByCourseId(Mono.just(newCourseRequestModel), course1.getCourseId());

        // assert
        StepVerifier
                .create(result)
                .expectNextMatches(courseResponseModel -> {
                    assertNotNull(courseResponseModel);
                    assertEquals(course1.getCourseId(), courseResponseModel.getCourseId());
                    assertEquals(course1.getCourseNumber(), courseResponseModel.getCourseNumber());
                    assertEquals(course1.getCourseName(), courseResponseModel.getCourseName());
                    assertEquals(course1.getNumHours(), courseResponseModel.getNumHours());
                    assertEquals(course1.getNumCredits(), courseResponseModel.getNumCredits());
                    assertEquals(course1.getDepartment(), courseResponseModel.getDepartment());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void whenDeleteCourse_thenReturnCourseResponseModel() {
        // arrange
        when(courseRepository.findCourseByCourseId(course1.getCourseId()))
                .thenReturn(Mono.just(course1));

        when(courseRepository.delete(any(Course.class)))
                .thenReturn(Mono.empty());

        // act
        Mono<CourseResponseModel> result = courseService.deleteCourseByCourseId(course1.getCourseId());

        // assert
        StepVerifier
                .create(result)
                .expectNextMatches(courseResponse -> courseResponse.getCourseId().equals(course1.getCourseId()))
                .verifyComplete();
    }




}