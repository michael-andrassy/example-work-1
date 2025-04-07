package demo.xmlassembly.service.doctypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** This enum globally declares all supported docTypes.
 *
 */
@Getter
@RequiredArgsConstructor
public enum EDocType {

    BORROWERDETAILS_ONLY("1201", "List of all Borrowers"),
    PLEDGE_LIFE_INSURANCE("1305", "Pledging Contract - Life Insurance");

    private final String omsDocTypeCode;
    private final String englishDesignation;
    // private final String germanDesignation;

} //enum
