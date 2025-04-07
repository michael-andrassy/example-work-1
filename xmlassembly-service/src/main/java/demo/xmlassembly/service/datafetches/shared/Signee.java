package demo.xmlassembly.service.datafetches.shared;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Signee {

    final ESigneeRole role;
    final String fullname;
}
