package com.champlain.enrollmentsservice;

import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseResponseModel;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;
import static org.mockserver.verify.VerificationTimes.exactly;

public class MockServerConfigCoursesService {
    private static final Integer MOCK_SERVER_PORT = 7003;

    private final ClientAndServer clientAndServer;

    private final MockServerClient mockServerClient = new MockServerClient("localhost", MOCK_SERVER_PORT);

    public MockServerConfigCoursesService() {
        this.clientAndServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT);
    }

    public void registerGetCourse1ByCourseIdEndpoint() {
        CourseResponseModel courseResponseModel = CourseResponseModel.builder()
                .courseId("9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                .courseNumber("trs-075")
                .courseName("Web Services")
                .department("Computer Science")
                .numHours(45)
                .numCredits(3.0)
                .build();

        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/courses/" + "9a29fff7-564a-4cc9-8fe1-36f6ca9bc223")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"courseId\":\"9a29fff7-564a-4cc9-8fe1-36f6ca9bc223\",\"courseNumber\":\"trs-075\",\"courseName\":\"Web Services\",\"numHours\":45,\"numCredits\":3.0,\"department\":\"Computer Science\"}"));


    }

    public void stopServer() {
        if (this.clientAndServer != null)
            this.clientAndServer.stop();
    }
}