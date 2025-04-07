package demo.xmlassembly.service.controllers.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Builder
@Data
@Jacksonized
public class Individual {

    private final String salutation;
    private final String fullname;

    private final String street;
    private final String city;
    private final String postCode;
    private final String country;

} //class
