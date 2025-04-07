package demo.xmlassembly.service.clients.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PledgedPortfolioCollateralRecord {

    private String portfolioHolder;
    private List<PledgedPortfolioPositionRecord> positions;

} //class
