package demo.xmlassembly.service.clients;


import demo.xmlassembly.service.clients.dtos.LifeInsuranceCollateralRecord;
import demo.xmlassembly.service.clients.dtos.PledgedPortfolioCollateralRecord;
import demo.xmlassembly.service.utils.Name2KeyConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

public interface CollateralsServiceClient {

    String mapFullname2Key(String fullname);

    List<LifeInsuranceCollateralRecord> getLifeInsuranceCollaterals(String dossierId, String key);

    List<PledgedPortfolioCollateralRecord> getPledgedPortfolioCollaterals(String dossierId, String key);

} //intf

@Slf4j
@Component
class CollateralsServiceClientImpl extends ARestClientComponent implements CollateralsServiceClient {

    private final Name2KeyConverter name2KeyConverter;

    private final String baseUrl;

    public CollateralsServiceClientImpl(
            Name2KeyConverter name2KeyConverter,
            WebClient.Builder webClientBuilder,
            @Value("${external.service.collaterals.url}") String baseUrl) {

        super( webClientBuilder.build() );

        this.name2KeyConverter = name2KeyConverter;

        this.baseUrl = baseUrl;
    } //ctor

    @Override
    public String mapFullname2Key(String fullname) {

        return name2KeyConverter.mapFullname2Key(fullname);
    } //m

    @Override
    public List<LifeInsuranceCollateralRecord> getLifeInsuranceCollaterals(String dossierId, String key) {

        // Build the URI from the baseUrl and path segments
        final URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment(dossierId, "lifeinsurances", key)
                .build()
                .toUri();

        return super.httpGetRecordList(uri, LifeInsuranceCollateralRecord.class);
    } //m

    @Override
    public List<PledgedPortfolioCollateralRecord> getPledgedPortfolioCollaterals(String dossierId, String key) {

        // Build the URI from the baseUrl and path segments
        final URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment(dossierId, "portfolios", key)
                .build()
                .toUri();

        return super.httpGetRecordList(uri, PledgedPortfolioCollateralRecord.class);
    } //m

} //class
