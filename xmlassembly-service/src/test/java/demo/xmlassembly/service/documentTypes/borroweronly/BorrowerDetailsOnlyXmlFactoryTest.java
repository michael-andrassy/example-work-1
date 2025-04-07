package demo.xmlassembly.service.documentTypes.borroweronly;

import demo.xmlassembly.service.datafetches.borrower.BorrowerData;
import demo.xmlassembly.service.datafetches.shared.ESigneeRole;
import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import demo.xmlassembly.service.doctypes.borrowerdetails.BorrowerDetailsOnlyXmlFactory;
import demo.xmlassembly.service.doctypes.borrowerdetails.BorrowerDetailsOnlyConsolidatedData;
import demo.xmlassembly.service.utils.XmlTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/** This test targets the document-type specific creation of xml.
 *
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class BorrowerDetailsOnlyXmlFactoryTest {

    @Autowired
    BorrowerDetailsOnlyXmlFactory borrowerDetailsOnlyXmlFactory;

    @Autowired
    BorrowerDetailsOnlySharedFactory sharedFactory;

    @Autowired
    XmlTestUtils xmlTestUtils;

    @Test
    public void testXmlCreation1() {

        final BorrowerDetailsOnlyConsolidatedData consolidatedData = this.createDummyBorrowersOnlyConsolidatedData();

        final String xmlText = borrowerDetailsOnlyXmlFactory.produceXml( consolidatedData );

        log.info("Generated Xml:\n{}", xmlText);

        assertTrue( xmlTestUtils.elementExists(xmlText,  "/ns:document"), "top-level document node is missing");
        assertTrue( xmlTestUtils.elementExists(xmlText,  "/ns:document/ns:metadata"), "metadata node is missing");

        assertTrue( xmlTestUtils.elementExists(xmlText,  "/ns:document/ns:metadata/ns:document-type"), "document-type node is missing");
        assertTrue( xmlTestUtils.elementIsNotEmpty(xmlText,  "/ns:document/ns:metadata/ns:document-type"), "document-type node is missing");

        assertTrue( xmlTestUtils.elementExists(xmlText,  "/ns:document/ns:content"), "content node is missing");
        assertTrue( xmlTestUtils.elementExists(xmlText,  "/ns:document/ns:content/ns:Header"), "Header node is missing");

        assertTrue( xmlTestUtils.elementExists(xmlText,  "/ns:document/ns:content/ns:borrower"), "borrower node is missing");
        assertEquals(1,  xmlTestUtils.countElement(xmlText,  "/ns:document/ns:content/ns:borrower/ns:individual"), "unexpected number of borrower/individuals");

        assertTrue( xmlTestUtils.elementExists(xmlText,  "/ns:document/ns:content/ns:borrower/ns:individual[1]"), "first borrower/individual node is missing");
        assertTrue( xmlTestUtils.elementExists(xmlText,  "/ns:document/ns:content/ns:borrower/ns:individual[1]/ns:name"), "first borrower/individual/name node is missing");
        assertTrue( xmlTestUtils.elementIsNotEmpty(xmlText,  "/ns:document/ns:content/ns:borrower/ns:individual[1]/ns:name"), "first borrower/individual/name node is empty");
        assertEquals("Barbara Smith",  xmlTestUtils.getElementInnerText(xmlText,  "/ns:document/ns:content/ns:borrower/ns:individual[1]/ns:name"), "unexpected number of signatures/signature");
        assertNotEquals("John Smith",  xmlTestUtils.getElementInnerText(xmlText,  "/ns:document/ns:content/ns:borrower/ns:individual[1]/ns:name"), "unexpected number of signatures/signature");

        assertEquals(1,  xmlTestUtils.countElement(xmlText,  "/ns:document/ns:content/ns:signatures/ns:signature"), "unexpected number of signatures/signature");
        assertEquals("Barbara Smith",  xmlTestUtils.getElementInnerText(xmlText,  "/ns:document/ns:content/ns:signatures/ns:signature[1]/ns:name"), "unexpected number of signatures/signature");

    } //m

    private BorrowerDetailsOnlyConsolidatedData createDummyBorrowersOnlyConsolidatedData() {

        final DocumentCreationTask documentCreationTask = sharedFactory.createMockDocumentCreationTask();

        final BorrowerData borrowerData = sharedFactory.createMockFetchedBorrowerDetailsOnlyData().getBorrowerData();

        final BorrowerDetailsOnlyConsolidatedData consolidatedData = BorrowerDetailsOnlyConsolidatedData.builder().
                documentCreationTask( documentCreationTask ).
                borrowerData( borrowerData ).
                borrowerOnlySignees( borrowerData.getPotentialSignees().stream().filter(S -> S.getRole().equals( ESigneeRole.Borrower)).toList() ).
        build();

        return consolidatedData;
    } //m

} //m
