package demo.xmlassembly.service.datafetches.supplcollaterals;

import demo.xmlassembly.service.clients.CollateralsServiceClient;
import demo.xmlassembly.service.clients.dtos.LifeInsuranceCollateralRecord;
import demo.xmlassembly.service.services.data.Dossier;
import demo.xmlassembly.service.services.data.Person;
import demo.xmlassembly.service.setup.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface DFSupplementaryCollaterals {

    // arbitrary decision - while the underlying service returns results per person, this df returns all insurances for the dossier
    Map<String, List<LifeInsuranceCollateral>> fetchLifeInsuranceCollaterals(Dossier dossier);

} //class

@Slf4j
@RequiredArgsConstructor
@Component
class DFSupplementaryCollateralsImpl implements DFSupplementaryCollaterals {

    private final CollateralsServiceClient collateralsServiceClient;

    //for a productive scenario I would consider to also include a service_request_id into this method's signature, just to add it to the caching-key.
    //this would limit the caching effect to a single service_request

    @Cacheable(
            value = CacheConfig.ALL_DF_CACHE_NAME,
            key   = "'SupplCollateral_Life_' + #dossier.dossierId" //one cache for all supplementary collaterals - hence LifeInsurance in the name
    )
    @Override
    public Map<String, List<LifeInsuranceCollateral>> fetchLifeInsuranceCollaterals(Dossier dossier) {

        final List<String> allNames =
                dossier.getBorrower().getPersons().stream().
                        map(Person::getFullname).
                        toList();

        List<LifeInsuranceCollateralRecord> allRecords = new ArrayList<>(allNames.size());

        for( String name : allNames ) {

            final String key = collateralsServiceClient.mapFullname2Key(name);

            final List<LifeInsuranceCollateralRecord> lifeInsurancesCallResult =
                    collateralsServiceClient.getLifeInsuranceCollaterals(dossier.getDossierId().toString(), key);

            if (lifeInsurancesCallResult != null) {
                allRecords.addAll(lifeInsurancesCallResult);
            }
        } //for

        final Map<String, List<LifeInsuranceCollateral>> result = allRecords.stream().map(this::mapLifeInsuranceRecord).collect(Collectors.groupingBy(LifeInsuranceCollateral::getPolicyholderFullname));

        /*
        Map<String, List<LifeInsuranceCollateral>> result =
                dossier.getBorrower().getPersons().stream().map(P -> {

                    final String key = collateralsServiceClient.mapFullname2Key(P.getFullname());


                    return lifeInsurancesCallResult;
                }).
                filter(Objects::nonNull).           //some requests might return null
                flatMap(List::stream).              //consolidate list-of-lists to list
                map(this::mapLifeInsuranceRecord).  //map to the own datastructure
                collect(Collectors.groupingBy(LifeInsuranceCollateral::getPolicyholderFullname)); //group by policy holder

        */

        return result;
    } //m

    private LifeInsuranceCollateral mapLifeInsuranceRecord(LifeInsuranceCollateralRecord dfResult) {

        return LifeInsuranceCollateral.builder().
                policyNo( dfResult.getPolicyNo() ).
                policyholderFullname( dfResult.getPolicyholderFullname() ).
                build();
    } //m

} //class

