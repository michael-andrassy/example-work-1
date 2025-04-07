package demo.xmlassembly.service.documentTypes.borroweronly;

import demo.xmlassembly.service.datafetches.shared.ESigneeRole;
import demo.xmlassembly.service.datafetches.shared.Signee;
import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import demo.xmlassembly.service.doctypes.borrowerdetails.BorrowerDetailsOnlyDataConsolidation;
import demo.xmlassembly.service.doctypes.borrowerdetails.BorrowerDetailsOnlyConsolidatedData;
import demo.xmlassembly.service.doctypes.borrowerdetails.BorrowerDetailsOnlyFetchedData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/** This type of unit test is targeted at the business logic in the data consolidation stage.
 *
 */
@SpringBootTest
@ActiveProfiles("test")
public class BorrowerDetailsOnlyDataConsolidationTest {

    //Frankly - for this particular document type in the example writing a test
    //for data consolidation does not make a lot of sense - I've added it for the sake of completeness

    //However, if the data consolidation spec would prescribe certain orderings of the records, this could be tested here.

    @Autowired
    BorrowerDetailsOnlyDataConsolidation dataConsolidationComponent;

    @Autowired
    BorrowerDetailsOnlySharedFactory sharedFactory;


    @Test
    public void testDataConsolidation1() {

        final DocumentCreationTask documentCreationTask = sharedFactory.createMockDocumentCreationTask();
        final BorrowerDetailsOnlyFetchedData fetchedData = sharedFactory.createMockFetchedBorrowerDetailsOnlyData();

        final BorrowerDetailsOnlyConsolidatedData output = dataConsolidationComponent.consolidate( documentCreationTask, fetchedData );

        assertNotNull( output.getBorrowerData(), "borrower was null" );

        assertNotNull( output.getBorrowerData().getBorrowers(), "borrower was null" );
        assertEquals( fetchedData.getBorrowerData().getBorrowers().size(), output.getBorrowerData().getBorrowers().size(), "Number of borrowers unequal");

        assertNotNull( output.getBorrowerData().getOtherPersons(), "other-persons was null" );
        assertEquals( fetchedData.getBorrowerData().getBorrowers().size(), output.getBorrowerData().getBorrowers().size(), "Number of other-persons unequal");

        assertNotNull( output.getBorrowerOnlySignees(), "potential-signees was null" );
        assertFalse( output.getBorrowerOnlySignees().isEmpty(), "unexpected - potential-signees is empty");

        final Set<ESigneeRole> allDistinctSigneeRoles = output.getBorrowerOnlySignees().stream().map(Signee::getRole).collect(Collectors.toSet());
        assertEquals( 1, allDistinctSigneeRoles.size(), "expected 1 signee role only");
        assertTrue( allDistinctSigneeRoles.contains( ESigneeRole.Borrower ), "unexpected - no borrower has signed");

    } //m

} //class
