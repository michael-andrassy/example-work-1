package demo.xmlassembly.service.doctypes;

import demo.xmlassembly.service.services.SingleOmsRequestCreationResult;

/** For each docType we need one such implementation.
 *
 */
public interface DocumentTypeHandler {

    EDocType getApplicableDocumentType();

    SingleOmsRequestCreationResult process(DocumentCreationTask documentCreationTask);
}
