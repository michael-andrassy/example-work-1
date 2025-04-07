package demo.xmlassembly.service.datafetches.borrower;

import demo.xmlassembly.service.datafetches.shared.Signee;
import demo.xmlassembly.service.services.data.Person;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BorrowerData {

    final List<PersonWithPrivateData> borrowers;

    final List<Person> otherPersons;

    final List<Signee> potentialSignees;

} //class
