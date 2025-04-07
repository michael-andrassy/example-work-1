package demo.xmlassembly.service.clients.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PledgedPortfolioPositionRecord {

    private final String amount;
    private final String label;

} //class
