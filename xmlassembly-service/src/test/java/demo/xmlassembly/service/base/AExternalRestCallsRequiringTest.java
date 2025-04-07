package demo.xmlassembly.service.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

public class AExternalRestCallsRequiringTest extends AMockServerkBasedUnitTest {

    /**
     * Registers dynamic properties. The service-url properties are now set to point to our
     * mock server instance. Any test class extending this base class will automatically inherit this override.
     */
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {

        // We're bending all of the calls to external services to our localhost
        // and that second port we've opened for the MockServer
        final String hostAndPort = "http://localhost:" + mockServer.getPort();

        //these properties are configured in the application properties
        // and now we override these values to match our mock

        registry.add("external.service.privatedata.url", () -> hostAndPort + "/private-data");
        registry.add("external.service.familymembers.url", () -> hostAndPort + "/spouses/");
        registry.add("external.service.collaterals.url", () -> hostAndPort + "/collaterals");

    } //m

    /** This enum contains the functionality of building the uriPath
     *
     */
    @RequiredArgsConstructor
    public static enum EKnownEndpoints {

        GetPrivateData(1, A -> "/private-data/" + A[0]),
        GetSpouse(1, A -> "/spouses/" + A[0]),
        GetLifeinsuranceCollaterals(2, A -> "/collaterals/" + A[0] + "/lifeinsurances/" + A[1]),
        GetPledgedPortfoliosCollaterals(2, A -> "/collaterals/" + A[0] + "/portfolios/" + A[1])
        ;

        private final int countRequiredPathArgs;
        private final Function<String[], String> uriPathFactory;

        public String getURIPath(String... args) {

            if (args == null)
                throw new IllegalArgumentException(this.name() + " Cannot build path - args must not be null");

            if (args.length != this.countRequiredPathArgs)
                throw new IllegalArgumentException(this.name() + " Cannot build path - expected " + this.countRequiredPathArgs + " args, but got " + args.length);

            try {
                return this.uriPathFactory.apply(args);
            } catch (Exception e) {

                throw new RuntimeException(this.name() + " caught an exception when building path from " + Arrays.toString( args ), e);
            }
        } //m

    } //enum

    protected void setupRestCallMocks(DeclareEndpointMock... declarations) {

        Arrays.stream(declarations).forEach(D -> {

            if (D.resultResourcePath == null || D.resultResourcePath.isEmpty()) {

                this.setupMockResponse( D.resultResourcePath, "", 404);
            } else {

                final String jsonString = readResourceFile( D.resultResourcePath );

                this.setupMockResponse( D.getUriPath(), jsonString, 200);
            }
        });

    } //m


    @Data
    @Builder
    public static class DeclareEndpointMock {

        private final EKnownEndpoints endpoint;
        private final String uriPath;
        private final String resultResourcePath;

        public static DeclareEndpointMock as(String resourcePath, EKnownEndpoints endpoint, String... args) {

            if (endpoint.countRequiredPathArgs != args.length) {

                throw new IllegalArgumentException("Wrong number of arguments passed to DeclareEndpointMock " + endpoint.name() + ": " + Arrays.toString(args));
            }

            final String uriPath = endpoint.uriPathFactory.apply( args );

            return DeclareEndpointMock.builder().
                    endpoint(endpoint).
                    uriPath(uriPath).
                    resultResourcePath(resourcePath).
                    build();
        } //m
    } //class


    protected String readResourceFile(String resourcePath) {

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error reading resource file: " + resourcePath, e);
        }
    } //m

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts a JSON string into an instance of the specified class.
     *
     * @param json the JSON string
     * @param clazz the target class to construct
     * @param <T> the type parameter
     * @return an instance of T populated with data from the JSON string
     */
    protected <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON into " + clazz.getSimpleName(), e);
        }
    } //m

} //class
