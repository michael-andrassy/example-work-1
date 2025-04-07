package demo.xmlassembly.service.services;

import demo.xmlassembly.service.doctypes.EDocType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class SingleOmsRequestCreationResult {

    final UUID dossierId;
    final UUID clientId;

    final EDocType documentType;
    final Map<String, String> extractedParams;

    final String xml;

} //class
