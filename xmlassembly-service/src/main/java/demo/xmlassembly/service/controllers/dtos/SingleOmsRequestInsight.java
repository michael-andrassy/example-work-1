package demo.xmlassembly.service.controllers.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Jacksonized
public class SingleOmsRequestInsight {

    final UUID dossierId;
    final UUID clientId;
    final EBusinessTrigger businessTrigger;

    final String documentType;
    final Map<String, String> extractedParams;

    final String xml;
} //class
