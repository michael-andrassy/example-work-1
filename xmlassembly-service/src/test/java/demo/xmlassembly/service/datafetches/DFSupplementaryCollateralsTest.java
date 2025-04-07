package demo.xmlassembly.service.datafetches;

import demo.xmlassembly.service.base.AExternalRestCallsRequiringTest;
import demo.xmlassembly.service.datafetches.supplcollaterals.DFSupplementaryCollaterals;
import demo.xmlassembly.service.datafetches.supplcollaterals.LifeInsuranceCollateral;
import demo.xmlassembly.service.services.data.Borrower;
import demo.xmlassembly.service.services.data.Dossier;
import demo.xmlassembly.service.services.data.Person;
import demo.xmlassembly.service.utils.Name2KeyConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class DFSupplementaryCollateralsTest extends AExternalRestCallsRequiringTest {

    //in theory this test could mock those underlying rest client component,
    // but here I decided in favor of building on the earlier mocked rest calls

    @Autowired
    DFSupplementaryCollaterals dfSupplementaryCollaterals;

    @Autowired
    Name2KeyConverter name2KeyConverter;

    @Test
    public void testPledgedLifeinsurancesFetch1() {

        final Dossier dossier = setupScenario1();

        final Map<String, List<LifeInsuranceCollateral>> lifeinsurances4name = dfSupplementaryCollaterals.fetchLifeInsuranceCollaterals(dossier);

        final List<LifeInsuranceCollateral> johns = lifeinsurances4name.get("John Doe");
        final List<LifeInsuranceCollateral> janes = lifeinsurances4name.get("Jane Doe");

        assertNotNull(johns, "Couldn't find John's insurances");
        assertEquals(1, johns.size(), "Unexpected number for John's insurances");
        assertEquals("L-444-0123", johns.getFirst().getPolicyNo(), "Unexpected policyNo for John's insurance");

        assertNotNull(janes, "Couldn't find Jane's insurances");
        assertEquals(2, janes.size(), "Unexpected number for Janes's insurances");

    } //m

    private Dossier setupScenario1() {

        // the underlying data-fetch relies partially on data from the dossier and partially on
        // data fetched from external services, this setup-routine sets up a dossier and some matching rest-mocks

        final UUID dossierId = UUID.randomUUID();

        //the last param is the key - for this demo-project this key is a substitute for a technical id and it is generated from the fullname
        setupRestCallMocks(
                //get all life insurances John has pledged
                AExternalRestCallsRequiringTest.DeclareEndpointMock.as(
                        "dftests/supplcollaterals/john_lifeinsurances.json",
                        AExternalRestCallsRequiringTest.EKnownEndpoints.GetLifeinsuranceCollaterals,
                        dossierId.toString(), name2KeyConverter.mapFullname2Key("John Doe")),
                //get all life insurances Jane has pledged
                AExternalRestCallsRequiringTest.DeclareEndpointMock.as(
                        "dftests/supplcollaterals/jane_lifeinsurances.json",
                        AExternalRestCallsRequiringTest.EKnownEndpoints.GetLifeinsuranceCollaterals,
                        dossierId.toString(), name2KeyConverter.mapFullname2Key("Jane Doe"))
        );

        return Dossier.builder().
                dossierId( dossierId ).
                clientId( UUID.randomUUID() ).
                borrower(
                        Borrower.builder().
                                clientId( UUID.randomUUID().toString() ).
                                persons(
                                        List.of(
                                                Person.builder().
                                                        salutation("Mr.").fullname("John Doe").
                                                        street("456 Elm Street").
                                                        postCode("0815").city("Metropolis").
                                                        country("Switzerland").
                                                        build(),
                                                Person.builder().
                                                        salutation("Ms.").fullname("Jane Doe").
                                                        street("456 Elm Street").
                                                        postCode("0815").city("Metropolis").
                                                        country("Switzerland").
                                                        build()
                                        )
                                ).
                                build()
                ).
                build();
    } //m


} //class
