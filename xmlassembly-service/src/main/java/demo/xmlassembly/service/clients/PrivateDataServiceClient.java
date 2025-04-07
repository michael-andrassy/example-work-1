package demo.xmlassembly.service.clients;


import demo.xmlassembly.service.clients.dtos.PersonPrivateDataRecord;
import demo.xmlassembly.service.utils.Name2KeyConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public interface PrivateDataServiceClient {

    String mapFullname2Key(String fullname);

    PersonPrivateDataRecord getPrivateData(String key);

} //intf

@Slf4j
@Component
class PrivateDataServiceClientImpl extends ARestClientComponent implements PrivateDataServiceClient {

    private final Name2KeyConverter name2KeyConverter;

    private final String baseUrl;

    public PrivateDataServiceClientImpl(
            Name2KeyConverter name2KeyConverter,
            WebClient.Builder webClientBuilder,
            @Value("${external.service.privatedata.url}") String baseUrl) {

        super( webClientBuilder.build() );

        this.name2KeyConverter = name2KeyConverter;

        //this.webClient = webClientBuilder.build();
        this.baseUrl = baseUrl;

    } //ctor

    @Override
    public String mapFullname2Key(String fullname) {

        return name2KeyConverter.mapFullname2Key(fullname);
    } //m

    @Override
    public PersonPrivateDataRecord getPrivateData(String key) {

        // Build the URI from the baseUrl and path segments
        final URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment( key )
                .build()
                .toUri();

        return super.httpGetRecord(uri, PersonPrivateDataRecord.class);
    } //m

} //class
