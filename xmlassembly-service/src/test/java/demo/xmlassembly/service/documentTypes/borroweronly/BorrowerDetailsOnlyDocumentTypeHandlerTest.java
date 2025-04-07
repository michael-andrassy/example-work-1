package demo.xmlassembly.service.documentTypes.borroweronly;


import demo.xmlassembly.service.base.AExternalRestCallsRequiringTest;
import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import demo.xmlassembly.service.doctypes.borrowerdetails.BorrowerDetailsOnlyDocumentTypeHandler;
import demo.xmlassembly.service.doctypes.borrowerdetails.BorrowerDetailsOnlyTrigger;
import demo.xmlassembly.service.services.MultipleDocumentCreationTask;
import demo.xmlassembly.service.services.SingleOmsRequestCreationResult;
import demo.xmlassembly.service.services.data.Borrower;
import demo.xmlassembly.service.services.data.Dossier;
import demo.xmlassembly.service.services.data.EBusinessContext;
import demo.xmlassembly.service.services.data.Person;
import demo.xmlassembly.service.utils.Name2KeyConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** This type of test is targeting the end-to-end production of a single document of the given type.
 *
 * <ul>
 *  <li>It would be reasonable to mock the data fetches for this type of test</li>
 *  <li>In general it would be advised to create the DocumentCreationTask manually, here I chose to use the trigger as it's a very simple one and I'd save some effort with the test setup</li>
 *  <li>Depending on the documents complexity - the produced xml could be tested here or in a separate dedicated test. </li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
public class BorrowerDetailsOnlyDocumentTypeHandlerTest extends AExternalRestCallsRequiringTest {

    // alternatively this would be a good opportunity to mock those data fetch components,
    // especially if those data fetches are not yet fully implemented.
    // In this case I went with mocking the rest-call counterparts again, to save me some effort

    @Autowired
    BorrowerDetailsOnlyDocumentTypeHandler borrowerDetailsOnlyDocumentTypeHandler;

    @Autowired
    BorrowerDetailsOnlyTrigger trigger;

    @Autowired
    BorrowerDetailsOnlySharedFactory sharedFactory;

    @Autowired
    Name2KeyConverter name2KeyConverter;


    @Test
    public void testEnd2EndProductionOfASingleDocument() {

        final Dossier dossier = setupScenario1();

        final MultipleDocumentCreationTask serviceRequest =
                sharedFactory.createInternalServiceRequest(EBusinessContext.SuperEarlyStage, dossier);

        //using the trigger to create me a DocumentCreationTask
        final List<DocumentCreationTask> documentCreationTasks = trigger.getDocumentsToBeBuilt(serviceRequest);

        assertEquals(1, documentCreationTasks.size(), "Unexpected number of document creation tasks");
        //in this case, I know that only one document will be triggered and the trigger is trivial - hence using the trigger in this test made sense
        final DocumentCreationTask documentCreationTask = documentCreationTasks.getFirst();


        final SingleOmsRequestCreationResult singleResultingOmsRequest = borrowerDetailsOnlyDocumentTypeHandler.process(documentCreationTask);

        assertEquals(documentCreationTask.getDossier().getDossierId(), singleResultingOmsRequest.getDossierId(), "unexpected dossier id" );
        assertEquals(documentCreationTask.getDossier().getClientId(), singleResultingOmsRequest.getClientId(), "unexpected client id" );
        assertEquals(documentCreationTask.getDocType(), singleResultingOmsRequest.getDocumentType(), "unexpected document type" );

        assertNotNull(singleResultingOmsRequest.getXml(), "produced xml was null" );
        assertFalse(singleResultingOmsRequest.getXml().isBlank(), "produced xml was blank" );
        assertTrue(singleResultingOmsRequest.getXml().trim().startsWith("<"), "seems like the produced xml is malformed" );

        // Id' rather test the produced xml elsewhere in detail, based on carefully selected consolidated data
        // so, for this test the xml is getting smoke tested only
    } //m

    private Dossier setupScenario1() {

        // the underlying data-fetch relies partially on data from the dossier and partially on
        // data fetched from external services, this setup-routine sets up a dossier and some matching rest-mocks

        //the last param is the key - for this demo-project this key is a substitute for a technical id and it is generated from the fullname
        setupRestCallMocks(
                DeclareEndpointMock.as("dftests/borrower/john_wdetails.json", EKnownEndpoints.GetPrivateData, name2KeyConverter.mapFullname2Key("John Doe")),
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
