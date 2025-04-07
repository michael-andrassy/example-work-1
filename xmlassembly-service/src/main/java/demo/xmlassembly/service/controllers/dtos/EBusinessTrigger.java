package demo.xmlassembly.service.controllers.dtos;

/**  An enum to encode a state of the process from which the doc-generaton task came.
 *   This is a simplification of real business logic - good enough for this demo.
 */
public enum EBusinessTrigger {

    SUPER_EARLY_STAGE,
    CONTRACT_READY;
}
