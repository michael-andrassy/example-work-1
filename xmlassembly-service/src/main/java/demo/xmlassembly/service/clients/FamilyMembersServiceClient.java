package demo.xmlassembly.service.clients;


import demo.xmlassembly.service.clients.dtos.PersonDataRecord;
import demo.xmlassembly.service.utils.Name2KeyConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public interface FamilyMembersServiceClient {

    String mapFullname2Key(String fullname);

    PersonDataRecord getSpouseOf(String key);

} //intf

@Slf4j
@Component
class FamilyMembersServiceClientImpl extends ARestClientComponent implements FamilyMembersServiceClient {

    private final Name2KeyConverter name2KeyConverter;

    private final String baseUrl;

    public FamilyMembersServiceClientImpl(
            Name2KeyConverter name2KeyConverter,
            WebClient.Builder webClientBuilder,
            @Value("${external.service.familymembers.url}") String baseUrl) {

        super( webClientBuilder.build() );

        this.name2KeyConverter = name2KeyConverter;

        this.baseUrl = baseUrl;
    } //ctor

    @Override
    public String mapFullname2Key(String fullname) {

        return name2KeyConverter.mapFullname2Key(fullname);
    } //m

    @Override
    public PersonDataRecord getSpouseOf(String key) {

        log.debug("Rest-Client - Fetching information regarding spouses {}", key);

        final URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment( key )
                .build()
                .toUri();

        return super.httpGetRecord(uri, PersonDataRecord.class);
    } //m

} //class
