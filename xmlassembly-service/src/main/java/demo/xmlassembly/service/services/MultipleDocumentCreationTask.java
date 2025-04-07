package demo.xmlassembly.service.services;

import demo.xmlassembly.service.services.data.Dossier;
import demo.xmlassembly.service.services.data.EBusinessContext;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultipleDocumentCreationTask {

    EBusinessContext businessContext;

    Dossier dossier;

} //class
