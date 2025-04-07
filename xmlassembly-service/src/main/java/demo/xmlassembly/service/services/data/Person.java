package demo.xmlassembly.service.services.data;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Person {

    private String salutation;
    private String fullname;

    private String street;
    private String city;
    private String postCode;
    private String country;

} //class
