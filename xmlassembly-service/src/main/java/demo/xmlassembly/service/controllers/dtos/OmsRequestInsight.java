package demo.xmlassembly.service.controllers.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@Jacksonized
public class OmsRequestInsight {

    private final UUID dossierId;
    private final UUID clientId;
    private final EBusinessTrigger businessTrigger;

    private final List<SingleOmsRequestInsight> omsRequests;

} //m
