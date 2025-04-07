package demo.xmlassembly.service.datafetches.supplcollaterals;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LifeInsuranceCollateral {

    private final String policyNo;
    private final String policyholderFullname;
} //class
