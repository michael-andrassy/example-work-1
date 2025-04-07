package demo.xmlassembly.service.clients.dtos;

import lombok.Builder;
import lombok.Data;

/** It is indeed another copy of that person data record.
 * In this given example this record definition is part of that external services api.
 */
@Builder
@Data
public class PersonDataRecord {

    private String salutation;
    private String fullname;

    private String street;
    private String city;
    private String postCode;
    private String country;

} //class
