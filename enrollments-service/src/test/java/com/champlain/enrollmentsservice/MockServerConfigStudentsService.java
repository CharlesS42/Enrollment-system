package com.champlain.enrollmentsservice;

import com.champlain.enrollmentsservice.domainclientlayer.Courses.CourseResponseModel;
import com.champlain.enrollmentsservice.domainclientlayer.Students.StudentResponseModel;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.MediaType;
import org.springframework.http.HttpStatus;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

public class MockServerConfigStudentsService {

    private static final Integer MOCK_SERVER_PORT = 7002;

    public final static String NON_EXISTING_STUDENTID = "c3540a89-cb47-4c96-888e-ff96708db4j4";

    private final ClientAndServer clientAndServer;

    private final MockServerClient mockServerClient = new MockServerClient("localhost", MOCK_SERVER_PORT);


    public MockServerConfigStudentsService() {
        this.clientAndServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT);
    }

    public void stopServer() {
        if (this.clientAndServer != null)
            this.clientAndServer.stop();
    }

    public void registerGetStudent1ByStudentIdEndpoint() {
        StudentResponseModel studentResponseModel = StudentResponseModel.builder()
                .studentId("c3540a89-cb47-4c96-888e-ff96708db4d8")
                .firstName("Christine")
                .lastName("Gerard")
                .program("Computer Science")
                .stuff("stuff")
                .build();

        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/students/" + "c3540a89-cb47-4c96-888e-ff96708db4d8")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_UTF_8.toString())
                                //use the courseResponseModel object to build the response body
                                .withBody(json("{\"studentId\":\"c3540a89-cb47-4c96-888e-ff96708db4d8\",\"firstName\":\"Christine\",\"lastName\":\"Gerard\",\"program\":\"Computer Science\",\"stuff\":\"stuff\"}", MediaType.APPLICATION_JSON))
                );
    }

    public void registerGetStudent_NonExisting_ByStudentIdEndpoint() {
        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/v1/students/" + NON_EXISTING_STUDENTID)
                )
                .respond(
                        response()
                                .withStatusCode(HttpStatus.NOT_FOUND.value())
                );
    }
}