package demo.xmlassembly.service.services.data;

import demo.xmlassembly.service.controllers.dtos.EBusinessTrigger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/** This enum is supposed to encode some process-state, this service will generate documents depending on the process state.
 *  This nothing but a simplification, relevant to this example only.
 */
@Getter
@RequiredArgsConstructor
public enum EBusinessContext {

    SuperEarlyStage(EBusinessTrigger.SUPER_EARLY_STAGE),
    ContractReady(EBusinessTrigger.CONTRACT_READY);

    //questionable approach but it makes life easier for now
    private final EBusinessTrigger correspondingValueFromExternalDto;

    public static EBusinessContext from(EBusinessTrigger value) {

        return Arrays.stream(values()).filter(v -> v.correspondingValueFromExternalDto.equals(value)).findFirst().orElse(null);
    } //m
} //enum
