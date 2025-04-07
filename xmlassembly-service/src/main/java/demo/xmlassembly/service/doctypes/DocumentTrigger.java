package demo.xmlassembly.service.doctypes;

import demo.xmlassembly.service.services.MultipleDocumentCreationTask;

import java.util.List;

/** For each doc type we need one such trigger.
 *
 * In theory there could be multiple such triggers implemented per docType, but then again - why?
 * Also, no one could prevent you from triggering multiple document types with a single trigger-component, but - why would you.
 */
public interface DocumentTrigger {

    List<DocumentCreationTask> getDocumentsToBeBuilt(MultipleDocumentCreationTask request);
}
