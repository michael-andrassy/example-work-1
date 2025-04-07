package demo.xmlassembly.service.controllers;

import demo.xmlassembly.service.controllers.dtos.*;
import demo.xmlassembly.service.services.MultipleDocumentCreationTask;
import demo.xmlassembly.service.services.OmsRequestBuilderService;
import demo.xmlassembly.service.services.MultipleOmsRequestCreationResult;
import demo.xmlassembly.service.services.SingleOmsRequestCreationResult;
import demo.xmlassembly.service.services.data.EBusinessContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/actions")
public class JobController {

    private final OmsRequestBuilderService omsRequestBuilderService;


    @PostMapping("/peek_oms_request")
    public Mono<OmsRequestInsight> peekRequest(@RequestBody PrintRequest request) {

        //we like it clean
        final MultipleDocumentCreationTask internalRequest = mapRequest( request );

        // Process the request
        final MultipleOmsRequestCreationResult internalResult =
                omsRequestBuilderService.buildOMSRequest( internalRequest );

        //map back to api data structures
        final OmsRequestInsight result = mapResponse( internalResult );

        // Return the response wrapped in a Mono
        return Mono.just( result );
    } //m

    @PostMapping("/create_pdf")
    public Mono<ResponseEntity<String>> initiatePdfCreation(@RequestBody PrintRequest request) {

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("not implemented - use the endpoint /peek_oms_request instead of /create_pdf"));
    } //m

    private MultipleDocumentCreationTask mapRequest(PrintRequest request) {

        return MultipleDocumentCreationTask.builder().
                businessContext(EBusinessContext.from( request.getBusinessTrigger() )).
                dossier( mapDossier(request.getDossier()) ).
                build();
    } //m

    private demo.xmlassembly.service.services.data.Dossier mapDossier(Dossier dossier) {

        return demo.xmlassembly.service.services.data.Dossier.builder().
                dossierId(dossier.getDossierId()).
                clientId( dossier.getBorrower().getClientId() ).
                borrower( mapBorrower(dossier.getBorrower()) ).
                build();
    } //m

    private demo.xmlassembly.service.services.data.Borrower mapBorrower(Borrower borrower) {

        return demo.xmlassembly.service.services.data.Borrower.builder().
                persons( borrower.getBorrowerPersons().stream().map(this::mapIndividual).toList() ).
                build();
    } //m

    private demo.xmlassembly.service.services.data.Person mapIndividual(Individual individual) {

        return demo.xmlassembly.service.services.data.Person.builder().
                salutation( individual.getSalutation() ).
                fullname( individual.getFullname() ).
                street( individual.getStreet() ).
                postCode( individual.getPostCode() ).
                city( individual.getCity() ).
                country( individual.getCountry() ).
                build();
    } //m

    private OmsRequestInsight mapResponse(MultipleOmsRequestCreationResult internalResult) {

        final EBusinessTrigger businessTrigger = mapBusinessContext(internalResult.getBusinessContext());

        return OmsRequestInsight.builder().
                businessTrigger( businessTrigger ).
                dossierId( internalResult.getDossierId() ).
                clientId( internalResult.getClientId() ).
                omsRequests( internalResult.getCreatedOmsRequests().stream().map(R -> mapOmsRequest(R, businessTrigger)).toList() ).
                build();
    } //m

    private SingleOmsRequestInsight mapOmsRequest(SingleOmsRequestCreationResult omsRequest, EBusinessTrigger businessTrigger) {

        return SingleOmsRequestInsight.builder().
                documentType( omsRequest.getDocumentType().getOmsDocTypeCode() ).
                clientId( omsRequest.getClientId() ).
                dossierId( omsRequest.getDossierId() ).
                extractedParams( omsRequest.getExtractedParams() ).
                businessTrigger( businessTrigger ).
                xml( omsRequest.getXml() ).
                build();
    } //m

    private EBusinessTrigger mapBusinessContext(EBusinessContext businessContext) {

        return businessContext.getCorrespondingValueFromExternalDto();
    } //m

} //class
