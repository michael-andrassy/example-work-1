package demo.xmlassembly.service.controllers.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@Jacksonized
public class Borrower {

    private final UUID clientId;

    private final List<Individual> borrowerPersons;
} //class
