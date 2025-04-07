package demo.xmlassembly.service.documentTypes.borroweronly;

import demo.xmlassembly.service.base.AExternalRestCallsRequiringTest;
import demo.xmlassembly.service.controllers.dtos.OmsRequestInsight;
import demo.xmlassembly.service.controllers.dtos.PrintRequest;
import demo.xmlassembly.service.controllers.dtos.SingleOmsRequestInsight;
import demo.xmlassembly.service.utils.ConsolePrintUtils;
import demo.xmlassembly.service.utils.Name2KeyConverter;
import demo.xmlassembly.service.utils.SelfWebClient;
import demo.xmlassembly.service.utils.XmlTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Calls the rest endpoint from outside, thereby triggers the whole oms-request-creation.
 *
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BorrowerDetailsOnlyServiceLevelEnd2EndTest extends AExternalRestCallsRequiringTest {

    @LocalServerPort
    private int port;

    @Autowired
    SelfWebClient selfWebClient;

    @Autowired
    ConsolePrintUtils consolePrintUtils;

    @Autowired
    XmlTestUtils xmlTestUtils;

    @Autowired
    Name2KeyConverter name2KeyConverter;


    @BeforeEach
    protected void setPort() throws Exception {

        selfWebClient.setPort( this.port );
    }

    @Test
    public void testPrintRequestPost() {

        final PrintRequest printRequest4Test = setupScenario1();

        final OmsRequestInsight omsRequests = selfWebClient.callPeekOmsRequest( printRequest4Test );

        assertNotNull( omsRequests.getOmsRequests(), "List of actual oms requests is null" );
        assertEquals( 1, omsRequests.getOmsRequests().size(), "expected 1 oms request" );

        final SingleOmsRequestInsight omsRequest = omsRequests.getOmsRequests().getFirst();
        consolePrintUtils.print(omsRequest);

        final String xmlText = omsRequest.getXml();

        assertTrue( xmlTestUtils.elementExists(xmlText,  "/ns:document"), "top-level document node is missing");
        assertTrue( xmlTestUtils.elementExists(xmlText,  "/ns:document/ns:metadata"), "metadata node is missing");
        assertTrue( xmlTestUtils.elementExists(xmlText,  "/ns:document/ns:content"), "content node is missing");

        assertTrue( xmlTestUtils.elementExists(xmlText,  "/ns:document/ns:content/ns:borrower"), "borrower node is missing");
        assertEquals(2,  xmlTestUtils.countElement(xmlText,  "/ns:document/ns:content/ns:borrower/ns:individual"), "unexpected number of borrower/individuals");

        assertEquals(2,  xmlTestUtils.countElement(xmlText,  "/ns:document/ns:content/ns:signatures/ns:signature"), "unexpected number of signatures/signature");

    } //m

    private PrintRequest setupScenario1() {

        final PrintRequest printRequest4Test =
                fromJson(
                        readResourceFile("scenarios/scenario01/print_request01.json"),
                        PrintRequest.class
                );

        // The request contains a business-trigger and a minimal dossier
        // the underlying data-fetch relies partially on data from the dossier and partially on
        // data fetched from external services, this setup-routine sets up a dossier and some matching rest-mocks

        //the last param is the key - for this demo-project this key is a substitute for a technical id and it is generated from the fullname
        setupRestCallMocks(
                DeclareEndpointMock.as("scenarios/scenario01/john_wdetails.json", EKnownEndpoints.GetPrivateData, name2KeyConverter.mapFullname2Key("John Doe")),
                DeclareEndpointMock.as("scenarios/scenario01/jane_wdetails.json", EKnownEndpoints.GetPrivateData, name2KeyConverter.mapFullname2Key("Jane Doe")),

                DeclareEndpointMock.as("scenarios/scenario01/jane_wdetails.json", EKnownEndpoints.GetSpouse, name2KeyConverter.mapFullname2Key("John Doe")),
                DeclareEndpointMock.as("scenarios/scenario01/john_wdetails.json", EKnownEndpoints.GetSpouse, name2KeyConverter.mapFullname2Key("Jane Doe"))
        );

        return printRequest4Test;
    } //m

} //class
