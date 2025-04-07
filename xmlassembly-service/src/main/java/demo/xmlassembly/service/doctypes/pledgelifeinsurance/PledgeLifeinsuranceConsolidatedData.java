package demo.xmlassembly.service.doctypes.pledgelifeinsurance;

import demo.xmlassembly.service.datafetches.borrower.BorrowerData;
import demo.xmlassembly.service.datafetches.shared.Signee;
import demo.xmlassembly.service.datafetches.supplcollaterals.LifeInsuranceCollateral;
import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import lombok.Builder;
import lombok.Data;

import java.util.List;

// this data structure is purpose made for the given document type - its only purpose is to be well suited for the xml-creation

@Data
@Builder
public class PledgeLifeinsuranceConsolidatedData {

    /** Some of the contained attributes will be needed in the xml header - included the dossier, could have included a handful of attributes only. */
    private final DocumentCreationTask documentCreationTask;

    /** The document is addressed either at the bank or at the client */
    private final String addressee;

    /** Some of the fetched data can be used as is. */
    private final BorrowerData borrowerData;

    /** Here we selected one particular insurance policy. */
    private final LifeInsuranceCollateral matchingLifeInsuranceCollateral;

    /** This is the result of the business logic implemented in the data consolidation. */
    private final List<Signee> allSignees;

}
