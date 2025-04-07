package demo.xmlassembly.service.documentTypes.pledgelifeinsurance;

import demo.xmlassembly.service.base.AExternalRestCallsRequiringTest;
import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import demo.xmlassembly.service.doctypes.EDocType;
import demo.xmlassembly.service.doctypes.borrowerdetails.BorrowerDetailsOnlyTrigger;
import demo.xmlassembly.service.doctypes.pledgelifeinsurance.PledgeLifeinsuranceTrigger;
import demo.xmlassembly.service.services.MultipleDocumentCreationTask;
import demo.xmlassembly.service.services.data.EBusinessContext;
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

/** This test targets the document triggering logic for this document type.
 *
 */
@SpringBootTest
@ActiveProfiles("test")
public class PledgedLifeinsuranceTriggerTest extends AExternalRestCallsRequiringTest {

    //this particular trigger won't make rest calls - hence not rest-mock setup

    @Autowired
    PledgeLifeinsuranceTrigger trigger;

    @Autowired
    PledgedLifeinsuranceSharedFactory sharedFactory;

    @Autowired
    Name2KeyConverter name2KeyConverter;

    @Test
    public void testTriggerWorksForMatchingState() {

        final MultipleDocumentCreationTask serviceRequest = setupScenario1(EBusinessContext.ContractReady);


        final List<DocumentCreationTask> documentCreationTasks = trigger.getDocumentsToBeBuilt(serviceRequest);

        assertNotNull( documentCreationTasks, "trigger must not return null");
        assertEquals(2, documentCreationTasks.size(), "unexpected number of doc creation tasks");

        final Set<EDocType> allDocTypes = documentCreationTasks.stream().map(T -> T.getDocType()).collect(Collectors.toSet());

        assertEquals(1, allDocTypes.size(), "unexpected number of doc types created");
        assertEquals(EDocType.PLEDGE_LIFE_INSURANCE, documentCreationTasks.getFirst().getDocType(), "trigger returned creation tasks for unexpected type");

        final Set<String> allAddressees = documentCreationTasks.stream().
                map(T -> T.getAdditionalParams().get(PledgeLifeinsuranceTrigger.PARAM_ADDRESSEE)).
                collect(Collectors.toSet());

        assertEquals(2, allAddressees.size(), "unexpected number of addressees");
    } //m

    @Test
    public void testTriggerWorksForMismatchingState() {

        final MultipleDocumentCreationTask serviceRequest = setupScenario1(EBusinessContext.SuperEarlyStage);


        final List<DocumentCreationTask> documentCreationTasks = trigger.getDocumentsToBeBuilt(serviceRequest);

        assertNotNull( documentCreationTasks, "trigger must not return null");
        assertEquals(0, documentCreationTasks.size(), "unexpected number of doc creation tasks");
    } //m


    private MultipleDocumentCreationTask setupScenario1(EBusinessContext businessContext) {


        final MultipleDocumentCreationTask serviceRequest =
                sharedFactory.createInternalServiceRequest( businessContext );

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
