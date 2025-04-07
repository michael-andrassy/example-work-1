package demo.xmlassembly.service.controllers.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class PrintRequest {

    private final EBusinessTrigger businessTrigger;

    private final Dossier dossier;

} //class
