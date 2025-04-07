package demo.xmlassembly.service.datafetches.borrower;

import demo.xmlassembly.service.services.data.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
public class PersonWithPrivateData extends Person {

    final String birthdate;
    final String socialSecurityNumber;

} //class
