package demo.xmlassembly.service.xml;

import demo.xmlassembly.omsxml.gen.privatecustomer.Borrower;
import demo.xmlassembly.omsxml.gen.privatecustomer.Document;
import demo.xmlassembly.omsxml.gen.privatecustomer.Signatures;
import demo.xmlassembly.service.datafetches.borrower.PersonWithPrivateData;
import demo.xmlassembly.service.datafetches.shared.ESigneeRole;
import demo.xmlassembly.service.datafetches.shared.Signee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


/** This component is a place to aggregate document-marshalling related functions which will likely be needed for multiple document types.
 *
 */
@Slf4j
@Component
public class SchemaPrivateCustomersDocumentUtils {


    public void setBorrower(Document document, String borrowerClientId, List<PersonWithPrivateData> borrowerIndividuals) {

        Borrower xmlBorrower = new Borrower();

        xmlBorrower.setId( borrowerClientId );

        borrowerIndividuals.stream().
                map( I -> {

                    final Borrower.Individual result = new Borrower.Individual();
                    result.setSalutation( I.getSalutation() );
                    result.setName( I.getFullname() );
                    result.setStreet( I.getStreet() );
                    result.setCity( I.getCity() );
                    result.setPostalCode( I.getPostCode() );
                    result.setCountry( I.getCountry() );
                    result.setBirthdate( I.getBirthdate() );
                    result.setAhvNo( I.getSocialSecurityNumber() );

                    return result;
                }).
                forEach( I -> xmlBorrower.getIndividual().add( I ));

        document.getContent().setBorrower( xmlBorrower );

    } //m

    public void setSignatures(Document document, List<Signee> signees) {

        final Signatures signatures = new Signatures();

        signees.stream().map(S -> {

            final Signatures.Signature result = new Signatures.Signature();
            result.setName( S.getFullname() );
            result.setRole( map(S.getRole()) );

            return result;
        }).
        forEach(S -> signatures.getSignature().add( S ));

        document.getContent().setSignatures( signatures );
    } //m

    private String map(ESigneeRole role) {

        //of course, if it was for real, we'd like to have something more sophisticated than this -> template engine, language specific

        return switch (role) {
            case Borrower -> "Kreditnehmer/in";
            case Spouse -> "Ehepartner/in";
            case SupplementaryCollateralContributor -> "Sicherheitsgeber/in";
            default -> throw new IllegalStateException("Implementation gap cannot translate rolename " + role);
        };
    } //m

} //class
