package com.mehmetserin.iban.service;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Locale;
import java.util.Set;

@Service
public class BicIbanService {

    private static final Set<String> SEPA_COUNTRIES = Set.of(
            "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR", "DE", "GR", "HU",
            "IE", "IT", "LV", "LT", "LU", "MT", "NL", "PL", "PT", "RO", "SK", "SI", "ES",
            "SE", "IS", "LI", "NO", "CH", "MC", "SM", "AD", "VA", "GB");

    // ISO 13616 national length table (subset commonly used in demos).
    private static final java.util.Map<String, Integer> COUNTRY_LENGTH = java.util.Map.ofEntries(
            java.util.Map.entry("DE", 22),
            java.util.Map.entry("TR", 26),
            java.util.Map.entry("GB", 22),
            java.util.Map.entry("FR", 27),
            java.util.Map.entry("NL", 18),
            java.util.Map.entry("ES", 24),
            java.util.Map.entry("IT", 27),
            java.util.Map.entry("CH", 21),
            java.util.Map.entry("AT", 20),
            java.util.Map.entry("BE", 16)
    );

    public record IbanResult(String iban, boolean valid, String country, boolean sepa, String message) {}
    public record BicResult(String bic, boolean valid, String bankCode, String country, String location, String branch, String message) {}

    public IbanResult validateIban(String raw) {
        if (raw == null || raw.isBlank()) {
            return new IbanResult("", false, null, false, "IBAN is required.");
        }
        String iban = normalize(raw);
        if (iban.length() < 15 || iban.length() > 34) {
            return new IbanResult(iban, false, null, false, "IBAN length out of range.");
        }
        if (!iban.matches("[A-Z]{2}[0-9]{2}[A-Z0-9]+")) {
            return new IbanResult(iban, false, null, false, "IBAN format is invalid.");
        }
        String country = iban.substring(0, 2);
        Integer expectedLen = COUNTRY_LENGTH.get(country);
        if (expectedLen != null && iban.length() != expectedLen) {
            return new IbanResult(iban, false, country, SEPA_COUNTRIES.contains(country),
                    "IBAN length for " + country + " must be " + expectedLen + ".");
        }
        boolean mod97 = mod97Valid(iban);
        if (!mod97) {
            return new IbanResult(iban, false, country, SEPA_COUNTRIES.contains(country), "IBAN check digits failed mod-97.");
        }
        return new IbanResult(iban, true, country, SEPA_COUNTRIES.contains(country), "OK");
    }

    public BicResult validateBic(String raw) {
        if (raw == null || raw.isBlank()) {
            return new BicResult("", false, null, null, null, null, "BIC is required.");
        }
        String bic = normalize(raw);
        if (!(bic.length() == 8 || bic.length() == 11) || !bic.matches("[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}([A-Z0-9]{3})?")) {
            return new BicResult(bic, false, null, null, null, null, "BIC format is invalid.");
        }
        String bank = bic.substring(0, 4);
        String country = bic.substring(4, 6);
        String location = bic.substring(6, 8);
        String branch = bic.length() == 11 ? bic.substring(8) : "XXX";
        return new BicResult(bic, true, bank, country, location, branch, "OK");
    }

    private static String normalize(String value) {
        return value.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }

    private static boolean mod97Valid(String iban) {
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        StringBuilder numeric = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isDigit(c)) {
                numeric.append(c);
            } else {
                numeric.append(c - 'A' + 10);
            }
        }
        return new BigInteger(numeric.toString()).mod(BigInteger.valueOf(97)).intValue() == 1;
    }
}
