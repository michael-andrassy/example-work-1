package demo.xmlassembly.service.doctypes.borrowerdetails;

import demo.xmlassembly.service.datafetches.borrower.BorrowerData;
import lombok.Builder;
import lombok.Data;

// strictly speaking this datastructure does not make a whole lot of sense as one
// could just use that one BorrowerData record instead - here this datastructure serves
// mainly the purpose of demostrating the idea

@Data
@Builder
public class BorrowerDetailsOnlyFetchedData {

    private final BorrowerData borrowerData;

}
