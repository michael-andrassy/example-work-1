package demo.xmlassembly.service.clients.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
@Jacksonized
public class LifeInsuranceCollateralRecord {

    private final String policyNo;
    private final String policyholderFullname;
}
