package demo.xmlassembly.service.services;


import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import demo.xmlassembly.service.doctypes.DocumentTrigger;
import demo.xmlassembly.service.doctypes.DocumentTypeHandler;
import demo.xmlassembly.service.doctypes.EDocType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface OmsRequestBuilderService {

    MultipleOmsRequestCreationResult buildOMSRequest(MultipleDocumentCreationTask request);

} //intf

@Slf4j
@RequiredArgsConstructor
@Service
class OmsRequestBuilderServiceImpl implements OmsRequestBuilderService {

    //dependency injection will do this for us
    final List<DocumentTrigger> allDocumentTriggers;
    final List<DocumentTypeHandler> allDocumentTypeSpecificHandlers;

    @Override
    public MultipleOmsRequestCreationResult buildOMSRequest(MultipleDocumentCreationTask request) {

        // note that there is no hint of parallelism at this level,
        // while it would easily be feasible such parallelism is likely to
        // reduce the effectiveness of the caching around the data fetches

        // also, in the data fetches I mentioned a requestId to be appended to
        // the caching keys, this request Id could be generated here - random UUID

        log.info("Received request to create OMS requests, client={}, dossier={}, {}",
                request.getDossier().getClientId(), request.getDossier().getDossierId(), request.getBusinessContext());

        //stage 1: figure out which single documents need to be created

        //present the request to all document-triggers and unify their results
        final List<DocumentCreationTask> allDocumentCreationTasks =
                allDocumentTriggers.stream().
                        map(T -> T.getDocumentsToBeBuilt( request )).flatMap(List::stream).
                        toList();

        //stage 2: create an OMS-request for each of these document creation tasks

        final Map<EDocType, DocumentTypeHandler> mapDocType2Handler = getDocType2HandlerMap();

        //for the sake of nice console output, no parallelism here -> allDocumentCreationTasks.parallelStream()
        final List<SingleOmsRequestCreationResult> allOmsRequests = allDocumentCreationTasks.stream().
                map(T -> {

                    final DocumentTypeHandler handler = mapDocType2Handler.get(T.getDocType());
                    if (handler == null) {
                        throw new RuntimeException("Cannot handle document type " + T.getDocType());
                    }

                    final SingleOmsRequestCreationResult omsRequest4Document = handler.process(T);
                    return omsRequest4Document;
                }).
                toList();

        return MultipleOmsRequestCreationResult.builder().
                dossierId( request.getDossier().getDossierId() ).
                clientId( request.getDossier().getClientId() ).
                businessContext( request.getBusinessContext() ).
                createdOmsRequests( allOmsRequests ).
                build();
    } //m

    private Map<EDocType, DocumentTypeHandler> getDocType2HandlerMap() {

        Map<EDocType, DocumentTypeHandler> result = new HashMap<>();

        allDocumentTypeSpecificHandlers.
                forEach(H -> {

                    if (result.put(H.getApplicableDocumentType(), H) != null)
                        throw new RuntimeException("Non-Unique DocType implementation: " + H.getApplicableDocumentType());
                } );

        return Collections.unmodifiableMap( result );
    } //m

} //class
