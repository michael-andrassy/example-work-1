package demo.xmlassembly.service.doctypes.pledgelifeinsurance;

import demo.xmlassembly.service.datafetches.borrower.BorrowerData;
import demo.xmlassembly.service.datafetches.borrower.DFBorrower;
import demo.xmlassembly.service.datafetches.supplcollaterals.DFSupplementaryCollaterals;
import demo.xmlassembly.service.datafetches.supplcollaterals.LifeInsuranceCollateral;
import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import demo.xmlassembly.service.doctypes.DocumentTypeHandler;
import demo.xmlassembly.service.doctypes.EDocType;
import demo.xmlassembly.service.services.SingleOmsRequestCreationResult;
import demo.xmlassembly.service.services.data.Dossier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/** Orchestrates the components of a single document type.
 * The document type specific trigger was run earlier by the framework.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PledgeLifeinsuranceDocumentTypeHandler implements DocumentTypeHandler {

    private final DFBorrower dfBorrower;
    private final DFSupplementaryCollaterals dfSupplementaryCollaterals;

    private final PledgeLifeinsuranceDataConsolidation pledgeLifeinsuranceDataConsolidation;

    private final PledgeLifeinsuranceXmlFactory pledgeLifeinsuranceXmlFactory;

    @Override
    public EDocType getApplicableDocumentType() {

        return EDocType.PLEDGE_LIFE_INSURANCE;
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

        //packaging the data-fetch for the borrower into a CompletableFuture
        final CompletableFuture<BorrowerData> futureFetchBorrower =
                CompletableFuture.supplyAsync(
                    () -> dfBorrower.fetchBorrowerData(dossier, true, true)
                );

        //packaging the data-fetch for the lifeinsurances into a CompletableFuture
        CompletableFuture<Map<String, List<LifeInsuranceCollateral>>> futureFetchLifeInsurances =
                CompletableFuture.supplyAsync(
                    () -> dfSupplementaryCollaterals.fetchLifeInsuranceCollaterals(dossier)
                );

        //=== Perform the data fetch in parallel

        // --> here we have an example that _could_ actually benefit from parallelism;
        //     think twice though, it seems worthwhile only if the individual calls take significant time

        // Wait for all futures to complete - this particular example with one data-fetch only
        // does not require going parallel - did it anyway, just to show the concept
        final CompletableFuture<?>[] dfFuturesArray = new CompletableFuture[]{
                futureFetchBorrower,
                futureFetchLifeInsurances
        };
        //final List<CompletableFuture<BorrowerData>> futures = List.of(futureFetchDossier);
        //CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        //=== Get the fetched data - parallel

        final BorrowerData borrowerData;
        final Map<String, List<LifeInsuranceCollateral>> lifeinsurances;
        try {

            CompletableFuture.allOf(dfFuturesArray).join();

            //get the data from all futures
            borrowerData = futureFetchBorrower.get();

            lifeinsurances = futureFetchLifeInsurances.get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Caught", e);
        }

//        final BorrowerData borrowerData = dfBorrower.fetchBorrowerData(dossier, true, true);
//        final Map<String, List<LifeInsuranceCollateral>> lifeinsurances = dfSupplementaryCollaterals.fetchLifeInsuranceCollaterals(dossier);


        //=== Consolidate the fetched data

        final PledgeLifeinsuranceFetchedData fetchedData =
                PledgeLifeinsuranceFetchedData.builder().
                        borrowerData(borrowerData).
                        mapPolicyHolderName2LifeInsurances(lifeinsurances).
                        build();

        final PledgeLifeinsuranceConsolidatedData consolidatedData =
                pledgeLifeinsuranceDataConsolidation.consolidate(documentCreationTask, fetchedData);

        //=== create the resulting XML from the consolidated data of one document

        final String producedXml = pledgeLifeinsuranceXmlFactory.produceXml( consolidatedData );

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
