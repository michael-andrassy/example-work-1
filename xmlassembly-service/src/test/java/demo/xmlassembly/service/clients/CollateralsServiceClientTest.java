package demo.xmlassembly.service.clients;

import demo.xmlassembly.service.base.AExternalRestCallsRequiringTest;
import demo.xmlassembly.service.clients.dtos.LifeInsuranceCollateralRecord;
import demo.xmlassembly.service.clients.dtos.PersonDataRecord;
import demo.xmlassembly.service.clients.dtos.PledgedPortfolioCollateralRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CollateralsServiceClientTest extends AExternalRestCallsRequiringTest {

    @Autowired
    CollateralsServiceClient collateralsServiceClient;

    @Test
    public void testGetLifeInsuraces() {

        setupRestCallMocks(
                DeclareEndpointMock.as("clients/collaterals/lifeinsurances01.json", EKnownEndpoints.GetLifeinsuranceCollaterals, "DOSSIER003", "15")
        );

        final List<LifeInsuranceCollateralRecord> lifeinsurancesOfGivenDossier = collateralsServiceClient.getLifeInsuranceCollaterals("DOSSIER003", "15");

        assertNotNull(lifeinsurancesOfGivenDossier, "List of returned lifeinsurances was null");
        assertNotEquals(0, lifeinsurancesOfGivenDossier.size(), "List of returned lifeinsurances was empty");
        assertEquals(3, lifeinsurancesOfGivenDossier.size(), "List of returned lifeinsurances size was not 3");

        final List<String> allPolicyHolders = lifeinsurancesOfGivenDossier.stream().map(LifeInsuranceCollateralRecord::getPolicyholderFullname).toList();
        final List<String> expectedPolicyHolders = List.of("Alice Smith", "Bob Johnson", "Carol Williams");

        expectedPolicyHolders.forEach(
                N -> assertTrue(allPolicyHolders.contains(N), "Missing policy holder " + N)
        );

    } //m

    @Test
    public void testGetPledgedPortfolios() {

        setupRestCallMocks(
                DeclareEndpointMock.as("clients/collaterals/portfolios01.json", EKnownEndpoints.GetPledgedPortfoliosCollaterals, "DOSSIER003", "23")
        );

        final List<PledgedPortfolioCollateralRecord> portfolios = collateralsServiceClient.getPledgedPortfolioCollaterals("DOSSIER003", "23");

        assertNotNull(portfolios, "List of returned portfolios was null");
        assertNotEquals(0, portfolios.size(), "List of returned portfolios was empty");
        assertEquals(2, portfolios.size(), "List of returned portfolios size was not 2");

        final List<String> allPolicyHolders = portfolios.stream().map(PledgedPortfolioCollateralRecord::getPortfolioHolder).toList();
        final List<String> portfolioHolders = List.of("John Doe", "Jane Smith");

        portfolioHolders.forEach(
                N -> assertTrue(allPolicyHolders.contains(N), "Missing portfolio holder " + N)
        );

    } //m

} //class

