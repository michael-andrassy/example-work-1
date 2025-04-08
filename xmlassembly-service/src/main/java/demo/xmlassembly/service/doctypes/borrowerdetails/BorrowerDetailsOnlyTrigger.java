package demo.xmlassembly.service.doctypes.borrowerdetails;


import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import demo.xmlassembly.service.doctypes.DocumentTrigger;
import demo.xmlassembly.service.doctypes.EDocType;
import demo.xmlassembly.service.services.MultipleDocumentCreationTask;
import demo.xmlassembly.service.services.data.EBusinessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/** A component that recognizes the need to create documents of a given type based on the request.
 *
 * For this document type the decision is based solely on the request and the included dossier data.
 *
 */
@Slf4j
@Component
public class BorrowerDetailsOnlyTrigger implements DocumentTrigger {

    private final List<EBusinessContext> applicableBusinessContexts = List.of(EBusinessContext.SuperEarlyStage);

    @Override
    public List<DocumentCreationTask> getDocumentsToBeBuilt(MultipleDocumentCreationTask request) {

        if (applicableBusinessContexts.contains( request.getBusinessContext() ) ) {

            log.debug("derived {} to produce in context {}", 1, request.getBusinessContext());

            return List.of(

                    DocumentCreationTask.builder().
                            docType(EDocType.BORROWERDETAILS_ONLY).
                            dossier( request.getDossier() ).
                            additionalParams( Map.of() ).
                            build()
            );

        } else return List.of();
    } //m

} //class
