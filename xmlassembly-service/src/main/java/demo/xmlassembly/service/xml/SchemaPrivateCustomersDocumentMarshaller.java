package demo.xmlassembly.service.xml;

import demo.xmlassembly.service.doctypes.EDocType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Component;
import demo.xmlassembly.omsxml.gen.privatecustomer.Document;
import org.w3c.dom.Comment;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;


/** This component has one purpose - xml-text from an instance of Document.
 *
 */
@Component
public class SchemaPrivateCustomersDocumentMarshaller {

    public String marshallToString(Document document, EDocType docType) {

        if (document == null) {
            throw new IllegalArgumentException("document is null");
        }

        final String xmlString;
        try {

            // Create a JAXBContext for the Document class.
            final JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
            final Marshaller marshaller = jaxbContext.createMarshaller();

            // Optionally format the output XML nicely.
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // Marshal the document instance to a StringWriter.
            final StringWriter writer = new StringWriter();
            marshaller.marshal(document, writer);

            // Convert the writer contents to a string.
            xmlString = writer.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate XML", e);
        }

        final String postProcessedXmlString = insertComments( xmlString, docType.name(), docType.getEnglishDesignation() );
        return postProcessedXmlString;
    } //m

    /**
     * Inserts two comment lines after line #6 of the XML text.
     * These comment are supposed to support testers when trying to make sense of these XMLs.
     *
     * @param xml the original XML string
     * @return the modified XML string with the inserted comments
     */
    private String insertComments(String xml, String internalDocTypeEnumValue, String plainEnglishDesignation) {
        // Split the XML string into lines (supports both LF and CRLF)
        String[] lines = xml.split("\\r?\\n");
        StringBuilder sb = new StringBuilder();

        // Process each line. When we reach the 6th line (index 5), insert the comment lines.
        for (int i = 0; i < lines.length; i++) {
            sb.append(lines[i]).append(System.lineSeparator());
            if (i == 5) { // After line 6 (i.e. index 5)
                // Insert the two comment lines with 8-space indent
                sb.append("        <!-- ").append(internalDocTypeEnumValue).append(" -->").append(System.lineSeparator());
                sb.append("        <!-- ").append(plainEnglishDesignation).append(" -->").append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

} //class
