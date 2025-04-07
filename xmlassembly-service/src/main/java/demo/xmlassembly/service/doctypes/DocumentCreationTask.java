package demo.xmlassembly.service.doctypes;

import demo.xmlassembly.service.services.data.Dossier;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DocumentCreationTask {

    final EDocType docType;

    final Dossier dossier;

    final Map<String, String> additionalParams;

} //class
