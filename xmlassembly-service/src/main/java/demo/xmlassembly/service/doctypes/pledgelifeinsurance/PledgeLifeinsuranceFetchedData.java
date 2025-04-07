package demo.xmlassembly.service.doctypes.pledgelifeinsurance;

import demo.xmlassembly.service.datafetches.borrower.BorrowerData;
import demo.xmlassembly.service.datafetches.supplcollaterals.LifeInsuranceCollateral;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

// contains all fetched data = input of the data consolidation

@Data
@Builder
public class PledgeLifeinsuranceFetchedData {

    private final BorrowerData borrowerData;

    private final Map<String, List<LifeInsuranceCollateral>> mapPolicyHolderName2LifeInsurances;

}
