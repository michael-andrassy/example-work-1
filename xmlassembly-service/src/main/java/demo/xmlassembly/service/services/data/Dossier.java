package demo.xmlassembly.service.services.data;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Dossier {

    UUID dossierId;
    UUID clientId;

    Borrower borrower;
}
