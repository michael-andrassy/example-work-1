package demo.xmlassembly.service.datafetches;

import demo.xmlassembly.service.base.AExternalRestCallsRequiringTest;
import demo.xmlassembly.service.datafetches.borrower.BorrowerData;
import demo.xmlassembly.service.datafetches.borrower.DFBorrower;
import demo.xmlassembly.service.datafetches.shared.ESigneeRole;
import demo.xmlassembly.service.services.data.Borrower;
import demo.xmlassembly.service.services.data.Dossier;
import demo.xmlassembly.service.services.data.Person;
import demo.xmlassembly.service.utils.Name2KeyConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DFBorrowerTest extends AExternalRestCallsRequiringTest {

    //in theory this test could mock those underlying rest client component,
    // but here I decided in favor of building on the earlier mocked rest calls

    @Autowired
    DFBorrower dfBorrower;

    @Autowired
    Name2KeyConverter name2KeyConverter;

    @Test
    public void testBorrowerDataFetch1() {

        final Dossier dossier = setupScenario1();

        final BorrowerData borrowerData = dfBorrower.fetchBorrowerData(dossier, true, true);

        assertNotNull(borrowerData, "borrowerData was null");

        assertNotNull(borrowerData.getBorrowers(), "borrowerData.borrowers was null");
        assertEquals(1, borrowerData.getBorrowers().size(), "borrowerData.borrowers has unexpected length");
        assertEquals("John Doe", borrowerData.getBorrowers().getFirst().getFullname(), "borrowerData.borrowers[0] has unexpected fullname");

        assertNotNull(borrowerData.getOtherPersons(), "borrowerData.otherPersons was null");
        assertEquals(1, borrowerData.getOtherPersons().size(), "borrowerData.otherPersons has unexpected length");
        assertEquals("Jane Doe", borrowerData.getOtherPersons().getFirst().getFullname(), "borrowerData.otherPersons[0] has unexpected fullname");

        assertNotNull(borrowerData.getPotentialSignees(), "borrowerData.psignees was null");
        assertEquals(2, borrowerData.getPotentialSignees().size(), "borrowerData.psignees has unexpected length");

        final List<ESigneeRole> signeeRoles = borrowerData.getPotentialSignees().stream().map(S -> S.getRole()).toList();
        assertTrue( signeeRoles.contains(ESigneeRole.Borrower), "No borrower signed");
        assertTrue( signeeRoles.contains(ESigneeRole.Spouse), "No spouse signed");
    } //m

    //scenario1 : 1 borrower + spouse = 1 borrower-persons + 1 additional person
    //scenario2 : couple-borrower = 2 borrower-persons, no additional persons
    //scenario3 : ...

    private Dossier setupScenario1() {

        // the underlying data-fetch relies partially on data from the dossier and partially on
        // data fetched from external services, this setup-routine sets up a dossier and some matching rest-mocks

        //the last param is the key - for this demo-project this key is a substitute for a technical id and it is generated from the fullname
        setupRestCallMocks(
                //get the private data of John Doe
                DeclareEndpointMock.as("dftests/borrower/john_wdetails.json", EKnownEndpoints.GetPrivateData, name2KeyConverter.mapFullname2Key("John Doe")),
                //get the spouse of John Doe
                DeclareEndpointMock.as("dftests/borrower/jane_nodetails.json", EKnownEndpoints.GetSpouse, name2KeyConverter.mapFullname2Key("John Doe"))
        );

        return Dossier.builder().
                dossierId( UUID.randomUUID() ).
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
                                                  build()
                                  )
                                ).
                                build()
                ).
                build();
    } //m

} //class
