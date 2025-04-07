package demo.xmlassembly.service.documentTypes.borroweronly;

import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import demo.xmlassembly.service.doctypes.EDocType;
import demo.xmlassembly.service.doctypes.borrowerdetails.BorrowerDetailsOnlyTrigger;
import demo.xmlassembly.service.services.MultipleDocumentCreationTask;
import demo.xmlassembly.service.services.data.EBusinessContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** This test targets the document triggering logic for this document type.
 *
 */
@SpringBootTest
@ActiveProfiles("test")
public class BorrowerDetailsOnlyTriggerTest {

    //this particular trigger won't make rest calls - hence not rest-mock setup

    @Autowired
    BorrowerDetailsOnlyTrigger trigger;

    @Autowired
    BorrowerDetailsOnlySharedFactory sharedFactory;

    @Test
    public void testTriggerWorksForMatchingState() {

        final MultipleDocumentCreationTask serviceRequest =
                sharedFactory.createInternalServiceRequest(EBusinessContext.SuperEarlyStage);

        final List<DocumentCreationTask> documentCreationTasks = trigger.getDocumentsToBeBuilt(serviceRequest);

        assertNotNull( documentCreationTasks, "trigger must not return null");
        assertEquals(1, documentCreationTasks.size(), "unexpected number of doc creation tasks");
        assertEquals(EDocType.BORROWERDETAILS_ONLY, documentCreationTasks.getFirst().getDocType(), "trigger returned creation tasks for unexpected type");

    } //m

    @Test
    public void testTriggerProducesNothingForMismatchingState() {

        final MultipleDocumentCreationTask serviceRequest =
                sharedFactory.createInternalServiceRequest(EBusinessContext.ContractReady);

        final List<DocumentCreationTask> documentCreationTasks = trigger.getDocumentsToBeBuilt(serviceRequest);

        assertNotNull( documentCreationTasks, "trigger must not return null");
        assertEquals(0, documentCreationTasks.size(), "unexpected number of doc creation tasks");

    } //m

} //m
