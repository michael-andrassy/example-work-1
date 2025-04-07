package demo.xmlassembly.service.documentTypes.pledgelifeinsurance;


import demo.xmlassembly.service.base.AExternalRestCallsRequiringTest;
import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import demo.xmlassembly.service.doctypes.EDocType;
import demo.xmlassembly.service.doctypes.borrowerdetails.BorrowerDetailsOnlyDocumentTypeHandler;
import demo.xmlassembly.service.doctypes.borrowerdetails.BorrowerDetailsOnlyTrigger;
import demo.xmlassembly.service.doctypes.pledgelifeinsurance.PledgeLifeinsuranceDataConsolidation;
import demo.xmlassembly.service.doctypes.pledgelifeinsurance.PledgeLifeinsuranceDocumentTypeHandler;
import demo.xmlassembly.service.doctypes.pledgelifeinsurance.PledgeLifeinsuranceTrigger;
import demo.xmlassembly.service.documentTypes.borroweronly.BorrowerDetailsOnlySharedFactory;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
public class PledgedLifeinsuranceDocumentTypeHandlerTest extends AExternalRestCallsRequiringTest {

    // alternatively this would be a good opportunity to mock those data fetch components,
    // especially if those data fetches are not yet fully implemented.
    // In this case I went with mocking the rest-call counterparts again, to save me some effort

    @Autowired
    PledgeLifeinsuranceTrigger trigger;

    @Autowired
    PledgeLifeinsuranceDocumentTypeHandler pledgeLifeinsuranceDocumentTypeHandler;


    @Autowired
    PledgedLifeinsuranceSharedFactory sharedFactory;

    @Autowired
    Name2KeyConverter name2KeyConverter;


    @Test
    public void testEnd2EndProductionOfASingleDocument() {

        final MultipleDocumentCreationTask serviceRequest = setupScenario1();


        final List<DocumentCreationTask> documentCreationTasks = trigger.getDocumentsToBeBuilt(serviceRequest);

        assertNotNull( documentCreationTasks, "trigger must not return null");
        assertEquals(2, documentCreationTasks.size(), "unexpected number of doc creation tasks");

        final DocumentCreationTask documentCreationTask = documentCreationTasks.getFirst();

        final SingleOmsRequestCreationResult singleResultingOmsRequest = pledgeLifeinsuranceDocumentTypeHandler.process(documentCreationTask);


        assertEquals(documentCreationTask.getDossier().getDossierId(), singleResultingOmsRequest.getDossierId(), "unexpected dossier id" );
        assertEquals(documentCreationTask.getDossier().getClientId(), singleResultingOmsRequest.getClientId(), "unexpected client id" );
        assertEquals(documentCreationTask.getDocType(), singleResultingOmsRequest.getDocumentType(), "unexpected document type" );

        assertNotNull(singleResultingOmsRequest.getXml(), "produced xml was null" );
        assertFalse(singleResultingOmsRequest.getXml().isBlank(), "produced xml was blank" );
        assertTrue(singleResultingOmsRequest.getXml().trim().startsWith("<"), "seems like the produced xml is malformed" );

    } //m

    private MultipleDocumentCreationTask setupScenario1() {


        final MultipleDocumentCreationTask serviceRequest =
                sharedFactory.createInternalServiceRequest( EBusinessContext.ContractReady );

        final UUID dossierId = serviceRequest.getDossier().getDossierId();;

        // The request contains a business-trigger and a minimal dossier
        // the underlying data-fetch relies partially on data from the dossier and partially on
        // data fetched from external services, this setup-routine sets up a dossier and some matching rest-mocks

        //the last param is the key - for this demo-project this key is a substitute for a technical id and it is generated from the fullname
        setupRestCallMocks(

                //get all life insurances John has pledged
                AExternalRestCallsRequiringTest.DeclareEndpointMock.as(
                        "scenarios/scenario01/john_lifeinsurances.json",
                        AExternalRestCallsRequiringTest.EKnownEndpoints.GetLifeinsuranceCollaterals,
                        dossierId.toString(), name2KeyConverter.mapFullname2Key("John Doe"))
        );

        return serviceRequest;
    } //m

} //class
