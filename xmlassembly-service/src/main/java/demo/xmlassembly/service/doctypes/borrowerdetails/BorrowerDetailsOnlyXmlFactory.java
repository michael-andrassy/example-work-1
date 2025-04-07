package demo.xmlassembly.service.doctypes.borrowerdetails;

import demo.xmlassembly.omsxml.gen.privatecustomer.Document;
import demo.xmlassembly.service.datafetches.borrower.PersonWithPrivateData;
import demo.xmlassembly.service.datafetches.shared.Signee;
import demo.xmlassembly.service.xml.SchemaPrivateCustomersDocumentInitializer;
import demo.xmlassembly.service.xml.SchemaPrivateCustomersDocumentMarshaller;
import demo.xmlassembly.service.xml.SchemaPrivateCustomersDocumentUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/** Produces a single xml document based on the classes that were generated from the xml-schema.
 *
 */@Slf4j
@RequiredArgsConstructor
@Component
public class BorrowerDetailsOnlyXmlFactory {

    private final SchemaPrivateCustomersDocumentInitializer schemaPrivateCustomersDocumentInitializer;
    private final SchemaPrivateCustomersDocumentMarshaller schemaPrivateCustomersDocumentMarshaller;
    private final SchemaPrivateCustomersDocumentUtils schemaPrivateCustomersDocumentUtils;

    private void mapDataForDocumentType(Document document, BorrowerDetailsOnlyConsolidatedData consolidatedData) {

        final List<PersonWithPrivateData> borrowerIndividuals = consolidatedData.getBorrowerData().getBorrowers();
        final String borrowerClientId = consolidatedData.getDocumentCreationTask().getDossier().getClientId().toString();

        schemaPrivateCustomersDocumentUtils.setBorrower(document, borrowerClientId, borrowerIndividuals);

        final List<Signee> signees = consolidatedData.getBorrowerOnlySignees();

        schemaPrivateCustomersDocumentUtils.setSignatures(document, signees);

    } //m

    public String produceXml(BorrowerDetailsOnlyConsolidatedData consolidatedData) {

        final String documentType = consolidatedData.getDocumentCreationTask().getDocType().getOmsDocTypeCode();
        final String clientId = consolidatedData.getDocumentCreationTask().getDossier().getClientId().toString();
        final String dossierId = consolidatedData.getDocumentCreationTask().getDossier().getDossierId().toString();

        final Document document = schemaPrivateCustomersDocumentInitializer.createInitializedDocument(documentType, clientId, dossierId);

        mapDataForDocumentType(document, consolidatedData );

        final String xmlString = schemaPrivateCustomersDocumentMarshaller.marshallToString( document, consolidatedData.getDocumentCreationTask().getDocType() );

        return xmlString;
    } //m

} //class

