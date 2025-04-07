package demo.xmlassembly.service.doctypes.borrowerdetails;

import demo.xmlassembly.service.datafetches.shared.ESigneeRole;
import demo.xmlassembly.service.datafetches.shared.Signee;
import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/** Implements all business logic for the given document type.
 *
 */
@Slf4j
@Component
public class BorrowerDetailsOnlyDataConsolidation {

    public BorrowerDetailsOnlyConsolidatedData consolidate(DocumentCreationTask documentCreationTask, BorrowerDetailsOnlyFetchedData fetchedData) {

        //specified business logic : only the borrowers themselves have to sign, spouses who are not contract partners don't have to sign

        final List<Signee> allPotentialSignees = fetchedData.getBorrowerData().getPotentialSignees();
        final List<Signee> borrowerOnlySignees = allPotentialSignees.stream().filter(S -> S.getRole().equals(ESigneeRole.Borrower)).toList();

        //this data structure now includes all data required for the xml creation

        final BorrowerDetailsOnlyConsolidatedData result =
                BorrowerDetailsOnlyConsolidatedData.builder().
                        documentCreationTask(documentCreationTask).
                        borrowerData( fetchedData.getBorrowerData() ).
                        borrowerOnlySignees( borrowerOnlySignees ).
                        build();

        return result;
    } //m

} //class