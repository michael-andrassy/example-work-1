package demo.xmlassembly.service.utils;

import demo.xmlassembly.service.controllers.dtos.OmsRequestInsight;
import demo.xmlassembly.service.controllers.dtos.PrintRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class SelfWebClient {

    //@LocalServerPort
    @Setter
    int port;

    //@Autowired
    private WebClient.Builder webClientBuilder = WebClient.builder();


    public OmsRequestInsight callPeekOmsRequest(PrintRequest printRequest) {

        assertNotNull(printRequest, "PrintRequest must not be null");

        // Build the WebClient with the base URL set to the random port of the embedded server.
        final WebClient webClient = webClientBuilder.baseUrl("http://localhost:" + port).build();

        // Send a POST request to the "/api/print" endpoint with the printRequest as the body.
        final ResponseEntity<OmsRequestInsight> responseEntity = webClient.post()
                .uri("/api/actions/peek_oms_request")
                .bodyValue(printRequest)
                .retrieve()
                .toEntity(OmsRequestInsight.class)
                .block();

        // Validate that the response is not null.
        assertNotNull(responseEntity, "Response entity must not be null");

        // Validate that the HTTP status code is 200 (OK).
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "HTTP status should be 200 OK");

        // Validate that the response body is not null.
        final OmsRequestInsight omsRequestInsight = responseEntity.getBody();
        assertNotNull(omsRequestInsight, "Response body (OmsRequestInsight) must not be null");

        return omsRequestInsight;
    } //m

} //class
