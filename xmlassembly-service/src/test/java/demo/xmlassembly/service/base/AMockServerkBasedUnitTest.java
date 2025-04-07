package demo.xmlassembly.service.base;



import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockserver.integration.ClientAndServer;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class AMockServerkBasedUnitTest {

    protected static ClientAndServer mockServer;

    @BeforeAll
    public static void startMockServer() {

        // Start MockServer on port 1080.
        mockServer = ClientAndServer.startClientAndServer(1080);
    } //m

    @AfterAll
    public static void stopMockServer() {

        if (mockServer != null) {
            mockServer.stop();
        }
    } //m

    /**
     * Sets up a mocked GET response for a given endpoint.
     * @param endpoint the endpoint path, e.g. "/123" for a full URL of http://localhost:1080/customers/123
     * @param jsonBody the JSON body to return (or null if you want to simulate a 404)
     * @param status the HTTP status code to return
     */
    protected void setupMockResponse(String endpoint, String jsonBody, int status) {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath(endpoint)
        ).respond(
                response()
                        .withStatusCode(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonBody != null ? jsonBody : "")
        );

    } //m

} //class
