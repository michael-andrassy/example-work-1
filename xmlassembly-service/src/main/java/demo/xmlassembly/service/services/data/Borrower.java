package demo.xmlassembly.service.services.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Borrower {

    final String clientId;

    final List<Person> persons;

}
