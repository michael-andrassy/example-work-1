package demo.xmlassembly.service.services;

import demo.xmlassembly.service.services.data.EBusinessContext;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MultipleOmsRequestCreationResult {

    final UUID dossierId;
    final UUID clientId;

    final EBusinessContext businessContext;

    final List<SingleOmsRequestCreationResult> createdOmsRequests;

} //class
