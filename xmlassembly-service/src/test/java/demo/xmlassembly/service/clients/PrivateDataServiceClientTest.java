package demo.xmlassembly.service.clients;

import demo.xmlassembly.service.base.AExternalRestCallsRequiringTest;
import demo.xmlassembly.service.clients.dtos.PersonPrivateDataRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PrivateDataServiceClientTest extends AExternalRestCallsRequiringTest {

    @Autowired
    PrivateDataServiceClient privateDataServiceClient;

    @Test
    public void testGetPrivateData() {

        setupRestCallMocks(
                DeclareEndpointMock.as("clients/privatedata/privatedata01.json", EKnownEndpoints.GetPrivateData, "1"),
                DeclareEndpointMock.as("clients/privatedata/privatedata02.json", EKnownEndpoints.GetPrivateData, "2")
        );

        final PersonPrivateDataRecord privateData1 = privateDataServiceClient.getPrivateData("1");
        assertNotNull(privateData1, "call 01 returned null");
        assertEquals("123-45-6789", privateData1.getSocialSecurityNo(), "call 01 returned unexpected data");

        final PersonPrivateDataRecord privateData2 = privateDataServiceClient.getPrivateData("2");
        assertNotNull(privateData2, "call 02 returned null");
        assertEquals("123-45-6347", privateData2.getSocialSecurityNo(), "call 02 returned unexpected data");

        final PersonPrivateDataRecord privateData3 = privateDataServiceClient.getPrivateData("3");
        assertNull(privateData3, "unexpected - call 03 did NOT return null");

    } //m

} //class
