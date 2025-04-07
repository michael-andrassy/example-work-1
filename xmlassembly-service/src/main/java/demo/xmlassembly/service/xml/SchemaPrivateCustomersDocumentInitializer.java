package demo.xmlassembly.service.xml;

import demo.xmlassembly.omsxml.gen.privatecustomer.Content;
import demo.xmlassembly.omsxml.gen.privatecustomer.Document;
import demo.xmlassembly.omsxml.gen.privatecustomer.Metadata;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.UUID;


/** This component has one purpose - create an instance of Document which has all attributes set that are dot doc-type specific.
 *
 */
@Component
public class SchemaPrivateCustomersDocumentInitializer {

    public Document createInitializedDocument(String documentType, String clientId, String dossierId) {

        try {

            final Document doc = new Document();

            final Metadata metadata = new Metadata();

            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            final XMLGregorianCalendar timestamp = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);

            metadata.setTimestamp( timestamp );
            metadata.setDocumentId(UUID.randomUUID().toString());
            metadata.setDocumentType( documentType );
            metadata.setClientId( clientId );
            metadata.setDossierId( dossierId );

            doc.setMetadata( metadata );

            final Content content = new Content();
            doc.setContent( content );

            content.setHeader("");

        return doc;

        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }

    } //m

} //m
