package demo.xmlassembly.service.utils;

import demo.xmlassembly.service.controllers.dtos.SingleOmsRequestInsight;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsolePrintUtils {

    public void print(SingleOmsRequestInsight omsRequest) {

        log.info("------------------------------------------------------------------------------------------");
        log.info("OMS Request {} -> {} ({})", omsRequest.getBusinessTrigger().name(), omsRequest.getDocumentType(), omsRequest.getClientId());
        log.info("\n" + omsRequest.getXml());
        log.info("------------------------------------------------------------------------------------------");



    } //m

} //class
