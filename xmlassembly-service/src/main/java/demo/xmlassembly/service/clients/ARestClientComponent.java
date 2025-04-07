package demo.xmlassembly.service.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ARestClientComponent {

    protected final WebClient webClient;

    protected ARestClientComponent(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Executes a GET request to the given URI expecting a single record of type T.
     * <p>
     * If the response status is 200 (OK), the response body is deserialized into the given recordClass.
     * For a 404 (NOT FOUND), null is returned.
     * For any other error, an exception is thrown with the error body.
     *
     * @param uri         the URI to call
     * @param recordClass the expected record type
     * @param <T>         the type of the record
     * @return an instance of T, or null if 404 was returned
     */
    protected <T> T httpGetRecord(URI uri, Class<T> recordClass) {

        log.debug("Calling GET {} -> {}", uri, recordClass.getSimpleName());

        return webClient.get()
                .uri(uri)
                .exchangeToMono(response -> {
                    HttpStatusCode status = response.statusCode();
                    if (status.equals(HttpStatus.OK)) {
                        return response.bodyToMono(recordClass);
                    } else if (status.equals(HttpStatus.NOT_FOUND)) {
                        return Mono.empty();
                    } else {
                        return response.bodyToMono(String.class)
                                .defaultIfEmpty("Unknown error")
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Error retrieving data: " + errorBody)));
                    }
                })
                .block();
    } //m

    /**
     * Executes a GET request to the given URI expecting a list of records of type T.
     * <p>
     * If the response status is 200 (OK), the response body is deserialized into a List of recordClass.
     * For a 404 (NOT FOUND), an empty list is returned.
     * For any other error, an exception is thrown with the error body.
     *
     * @param uri         the URI to call
     * @param recordClass the expected record type
     * @param <T>         the type of the records
     * @return a List of T, or null if 404 was returned
     */
    protected <T> List<T> httpGetRecordList(URI uri, Class<T> recordClass) {

        log.debug("Calling GET {} -> {}[]", uri, recordClass.getSimpleName());

        return webClient.get()
                .uri(uri)
                .exchangeToMono(response -> {
                    HttpStatusCode status = response.statusCode();
                    if (status.equals(HttpStatus.OK)) {
                        return response.bodyToFlux(recordClass).collectList();
                    } else if (status.equals(HttpStatus.NOT_FOUND)) {
                        return Mono.just(Collections.emptyList());
                    } else {
                        return response.bodyToMono(String.class)
                                .defaultIfEmpty("Unknown error")
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Error retrieving data: " + errorBody)));
                    }
                })
                //.subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .block();
    } //m

} //class
