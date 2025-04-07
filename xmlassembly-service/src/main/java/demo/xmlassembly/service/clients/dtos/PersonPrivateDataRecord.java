package demo.xmlassembly.service.clients.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PersonPrivateDataRecord {

    final String birthdate;
    final String socialSecurityNo;

}
