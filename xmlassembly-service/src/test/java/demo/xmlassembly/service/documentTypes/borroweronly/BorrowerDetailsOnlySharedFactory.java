package demo.xmlassembly.service.documentTypes.borroweronly;

import demo.xmlassembly.service.datafetches.borrower.BorrowerData;
import demo.xmlassembly.service.datafetches.borrower.PersonWithPrivateData;
import demo.xmlassembly.service.datafetches.shared.ESigneeRole;
import demo.xmlassembly.service.datafetches.shared.Signee;
import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import demo.xmlassembly.service.doctypes.EDocType;
import demo.xmlassembly.service.doctypes.borrowerdetails.BorrowerDetailsOnlyFetchedData;
import demo.xmlassembly.service.services.MultipleDocumentCreationTask;
import demo.xmlassembly.service.services.data.Borrower;
import demo.xmlassembly.service.services.data.Dossier;
import demo.xmlassembly.service.services.data.EBusinessContext;
import demo.xmlassembly.service.services.data.Person;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/** This is a component to be shared among all unit tests for this document type.
 *
 * Likely you'll also find a need for such component spanning all document types
 *
 */
@Component
public class BorrowerDetailsOnlySharedFactory {

    public MultipleDocumentCreationTask createInternalServiceRequest(EBusinessContext businessContext) {

        final Dossier dossier = createSimpleInternalDossier();

        return MultipleDocumentCreationTask.builder().
                businessContext( businessContext ).
                dossier( dossier ).
                build();
    } //class

    public MultipleDocumentCreationTask createInternalServiceRequest(EBusinessContext businessContext, Dossier dossier) {

        return MultipleDocumentCreationTask.builder().
                businessContext( businessContext ).
                dossier( dossier ).
                build();
    } //class

    public Dossier createSimpleInternalDossier() {

        return Dossier.builder().
                dossierId( UUID.randomUUID() ).
                clientId( UUID.randomUUID() ).
                borrower(
                        Borrower.builder().
                                clientId( UUID.randomUUID().toString() ).
                                persons(
                                        List.of(
                                                Person.builder().
                                                        salutation("Mr.").fullname("John Doe").
                                                        street("456 Elm Street").
                                                        postCode("0815").city("Metropolis").
                                                        country("Switzerland").
                                                        build()
                                        )
                                ).
                                build()
                ).
                build();
    } //m

    public DocumentCreationTask createMockDocumentCreationTask() {

        final DocumentCreationTask result = DocumentCreationTask.builder().
                docType(EDocType.BORROWERDETAILS_ONLY).
                additionalParams(Collections.emptyMap() ).
                dossier( this.createSimpleInternalDossier() ).
                build();

        return result;
    } //m

    public BorrowerDetailsOnlyFetchedData createMockFetchedBorrowerDetailsOnlyData() {

        return BorrowerDetailsOnlyFetchedData.builder().
                borrowerData(
                        BorrowerData.builder().
                                borrowers(
                                        List.of(
                                                PersonWithPrivateData.builder().
                                                        salutation("Mr.").fullname("Barbara Smith").
                                                        street("Milkyway 123").postCode("1234").city("Metropolis").
                                                        country("Switzerland").
                                                        build()
                                        )
                                ).
                                otherPersons(
                                        List.of(
                                                Person.builder().
                                                        salutation("Mr.").fullname("Jack Smith").
                                                        street("Milkyway 123").postCode("1234").city("Metropolis").
                                                        country("Switzerland").
                                                        build()
                                        )
                                ).
                                potentialSignees(
                                        List.of(
                                                Signee.builder().
                                                        fullname("Barbara Smith").role(ESigneeRole.Borrower).
                                                        build(),
                                                Signee.builder().
                                                        fullname("Jack Smith").role(ESigneeRole.Spouse).
                                                        build()
                                        )
                                ).
                                build()
                ).
                build();

    } //m

} //class
