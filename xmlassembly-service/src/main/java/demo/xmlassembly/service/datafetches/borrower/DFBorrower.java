package demo.xmlassembly.service.datafetches.borrower;

import demo.xmlassembly.service.clients.FamilyMembersServiceClient;
import demo.xmlassembly.service.clients.PrivateDataServiceClient;
import demo.xmlassembly.service.clients.dtos.PersonDataRecord;
import demo.xmlassembly.service.clients.dtos.PersonPrivateDataRecord;
import demo.xmlassembly.service.datafetches.shared.ESigneeRole;
import demo.xmlassembly.service.datafetches.shared.Signee;
import demo.xmlassembly.service.services.data.Dossier;
import demo.xmlassembly.service.services.data.Person;
import demo.xmlassembly.service.setup.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DFBorrower {

    BorrowerData fetchBorrowerData(Dossier dossier, boolean fetchSpouses, boolean fetchPrivateDetails);


} //class

@Slf4j
@RequiredArgsConstructor
@Component
class DFBorrowerImpl implements DFBorrower {

    private final PrivateDataServiceClient privateDataServiceClient;
    private final FamilyMembersServiceClient familyMembersServiceClient;

    //for a productive scenario I would consider to also include a service_request_id into this method's signature, just to add it to the caching-key.
    //this would limit the caching effect to a single service_request
    @Cacheable(
            value = CacheConfig.ALL_DF_CACHE_NAME,
            key   = "'Borrower_' + #dossier.dossierId + '_' + #fetchSpouses + '_' + #fetchPrivateDetails"
    )
    @Override
    public BorrowerData fetchBorrowerData(Dossier dossier, boolean fetchSpouses, boolean fetchPrivateDetails) {

        List<PersonWithPrivateData> borrowers = fetchBorrowerPersons(dossier, fetchPrivateDetails);

        final List<Person> spouses;
        if (fetchSpouses) {

            spouses = fetchSpouses( dossier );
        } else {

            spouses = null;
        }

        // -- time for some business logic - if a spouse is also listed as borrower, we'll remove that record (demo only! - criteria is fullname)
        final List<Person> nonBorrowerSpouses = getNonBorrowerSpouses(borrowers, spouses);
        final List<Signee> potentialSignees = createListOfPotentialSignees(borrowers, nonBorrowerSpouses);

        final BorrowerData result = BorrowerData.builder().
                borrowers(borrowers).
                otherPersons(nonBorrowerSpouses).
                potentialSignees(potentialSignees).
                build();

        return result;
    } //m

    private List<Signee> createListOfPotentialSignees(List<PersonWithPrivateData> borrowers, List<Person> spouses) {

        final List<Signee> signees = Stream.concat(
                borrowers.stream().map(B -> Signee.builder().fullname( B.getFullname() ).role( ESigneeRole.Borrower ).build() ),
                spouses.stream().map(S -> Signee.builder().fullname( S.getFullname() ).role( ESigneeRole.Spouse ).build() )
        ).toList();

        return signees;
    } //m

    private List<Person> getNonBorrowerSpouses(
            List<PersonWithPrivateData> borrowers,
            List<Person> spouses) {

        if (spouses == null || spouses.isEmpty()) {
            return List.of();
        }

        // Create a set of borrower full names for fast lookup.
        final Set<String> borrowerFullNames = borrowers.stream()
                .map(Person::getFullname)
                .collect(Collectors.toSet());

        // Filter spouses: include only those not present in the borrowers set.
        final List<Person> nonBorrowerSpouses = spouses.stream()
                .filter(spouse -> !borrowerFullNames.contains(spouse.getFullname()))
                .toList();

        return nonBorrowerSpouses;
    } //m

    private List<PersonWithPrivateData> fetchBorrowerPersons(Dossier dossier, boolean fetchPrivateDetails) {

        final List<PersonWithPrivateData> borrowers = dossier.getBorrower().getPersons().stream().
                map(P -> {

                    //this key is a simplification for this demo only - compared with uuid, its easier to use in the mock data
                    final String key = privateDataServiceClient.mapFullname2Key(P.getFullname());
                    final PersonPrivateDataRecord privateDataRec = privateDataServiceClient.getPrivateData(key);

                    //combining data from the dossier and data that was fetched from a service

                    final PersonWithPrivateData R = PersonWithPrivateData.builder().
                            salutation(P.getSalutation()).
                            fullname(P.getFullname()).
                            street(P.getStreet()).
                            postCode(P.getPostCode()).
                            city(P.getCity()).
                            country(P.getCountry()).
                            birthdate((privateDataRec != null) ? privateDataRec.getBirthdate() : null).
                            socialSecurityNumber((privateDataRec != null) ? privateDataRec.getSocialSecurityNo() : null).
                            build();

                    return R;

                }).toList();

        return borrowers;
    } //m

    private List<Person> fetchSpouses(Dossier dossier) {

        final List<Person> spouses = dossier.getBorrower().getPersons().stream().
                map(P -> {

                    //this key is a simplification for this demo only - compared with uuid, its easier to use in the mock data
                    final String key = familyMembersServiceClient.mapFullname2Key(P.getFullname());
                    final PersonDataRecord spouseDataRec = familyMembersServiceClient.getSpouseOf(key);

                    if (spouseDataRec == null) {

                        return null;

                    } else {

                        final Person S = Person.builder().
                                salutation(spouseDataRec.getSalutation()).
                                fullname(spouseDataRec.getFullname()).
                                street(spouseDataRec.getStreet()).
                                postCode(spouseDataRec.getPostCode()).
                                city(spouseDataRec.getCity()).
                                country(spouseDataRec.getCountry()).
                                build();

                        return S;
                    }

                }).
                filter(Objects::nonNull).
                toList();

        return spouses;
    } //m

} //class

