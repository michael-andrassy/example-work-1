package demo.xmlassembly.service.controllers.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder
@Data
@Jacksonized
public class Dossier {

    private final UUID dossierId;

    private final Borrower borrower;

} //class
