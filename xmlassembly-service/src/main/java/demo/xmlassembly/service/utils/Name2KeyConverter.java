package demo.xmlassembly.service.utils;

import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
public class Name2KeyConverter {

    public String mapFullname2Key(String fullname) {
        if (fullname == null) {
            return null;
        }
        // Normalize to separate letters and diacritical marks (e.g., Ü becomes U + ¨)
        String normalized = Normalizer.normalize(fullname, Normalizer.Form.NFD);
        // Remove diacritical marks (accents)
        String withoutAccents = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // Convert to uppercase and replace whitespace with underscores
        return withoutAccents.toUpperCase().replaceAll("\\s+", "_");
    } //m

} //class
