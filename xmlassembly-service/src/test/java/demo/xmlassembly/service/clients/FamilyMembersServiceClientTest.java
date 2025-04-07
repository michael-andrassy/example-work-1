package demo.xmlassembly.service.clients;

import demo.xmlassembly.service.base.AExternalRestCallsRequiringTest;
import demo.xmlassembly.service.clients.dtos.PersonDataRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class FamilyMembersServiceClientTest extends AExternalRestCallsRequiringTest {

    @Autowired
    FamilyMembersServiceClient familyMembersServiceClient;

    @Test
    public void testGetSpouseOf() {

        setupRestCallMocks(
                DeclareEndpointMock.as("clients/familymembers/person01.json", EKnownEndpoints.GetSpouse, "1")
        );

        final PersonDataRecord person1 = familyMembersServiceClient.getSpouseOf("1");
        assertNotNull(person1, "call 01 returned null");
        assertEquals("Springfield", person1.getCity(), "call 01 returned unexpected data");
    } //m


} //class
