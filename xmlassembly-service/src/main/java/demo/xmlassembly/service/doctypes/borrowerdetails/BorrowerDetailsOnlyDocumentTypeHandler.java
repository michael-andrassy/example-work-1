package demo.xmlassembly.service.doctypes.borrowerdetails;

import demo.xmlassembly.service.datafetches.borrower.BorrowerData;
import demo.xmlassembly.service.datafetches.borrower.DFBorrower;
import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import demo.xmlassembly.service.doctypes.DocumentTypeHandler;
import demo.xmlassembly.service.doctypes.EDocType;
import demo.xmlassembly.service.services.SingleOmsRequestCreationResult;
import demo.xmlassembly.service.services.data.Dossier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/** Orchestrates the components of a single document type.
 * The document type specific trigger was run earlier by the framework.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BorrowerDetailsOnlyDocumentTypeHandler implements DocumentTypeHandler {

    private final DFBorrower dfBorrower;

    private final BorrowerDetailsOnlyDataConsolidation borrowerDetailsOnlyDataConsolidation;

    private final BorrowerDetailsOnlyXmlFactory borrowerDetailsOnlyXmlFactory;

    @Override
    public EDocType getApplicableDocumentType() {

        return EDocType.BORROWERDETAILS_ONLY;
    } //m

    @Override
    public SingleOmsRequestCreationResult process(DocumentCreationTask documentCreationTask) {

        log.debug("starting document request-production for {} to produce in context {}", documentCreationTask.getDocType(),
                documentCreationTask.getAdditionalParams());

        // while this method looks like one could build a bit of generic framework around it,
        // this way it's much simpler - unifying the interfaces of data-fetches and other components
        // for uniform processing would introduce a number of compromises and extra effort

        final Dossier dossier = documentCreationTask.getDossier();

        //=== Setup the parallel data fetch

        // -->  each framework comes up with its own features to implement such parallelism,
        //      for productive use I'd also consider creating a dedicated thread pool for all parallel data fetches (of all document types)

        //packaging this call into a CompletableFuture
        final CompletableFuture<BorrowerData> futureFetchBorrower =
                CompletableFuture.supplyAsync(
                    () -> dfBorrower.fetchBorrowerData(dossier, true, true)
                );

        //=== Perform the data fetch in parallel

        // Wait for all futures to complete - this particular example with one data-fetch only
        // does not require going parallel - did it anyway, just to show the concept
        final CompletableFuture<?>[] dfFuturesArray = new CompletableFuture[]{
                futureFetchBorrower
        };
        //final List<CompletableFuture<BorrowerData>> futures = List.of(futureFetchDossier);
        //CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        //=== Get the fetched data - parallel

        final BorrowerData borrowerData;
        try {

            CompletableFuture.allOf(dfFuturesArray).join();

            //get the data from all futures
            borrowerData = futureFetchBorrower.get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Caught", e);
        }

        //=== Consolidate the fetched data

        final BorrowerDetailsOnlyFetchedData fetchedData =
                BorrowerDetailsOnlyFetchedData.builder().
                        borrowerData(borrowerData).
                        build();

        final BorrowerDetailsOnlyConsolidatedData consolidatedData =
                borrowerDetailsOnlyDataConsolidation.consolidate(documentCreationTask, fetchedData);

        //=== create the resulting XML from the consolidated data of one document

        final String producedXml = borrowerDetailsOnlyXmlFactory.produceXml( consolidatedData );

        //=== return result for a single produced document

        final SingleOmsRequestCreationResult result = SingleOmsRequestCreationResult.builder().
                documentType( documentCreationTask.getDocType() ).
                dossierId( documentCreationTask.getDossier().getDossierId() ).
                clientId( documentCreationTask.getDossier().getClientId() ).
                extractedParams( new HashMap<>( documentCreationTask.getAdditionalParams() ) ).
                xml( producedXml ).
                build();

        return result;
    } //m

} //class
