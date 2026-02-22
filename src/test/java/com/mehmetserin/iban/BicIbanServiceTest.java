package com.mehmetserin.iban;

import com.mehmetserin.iban.service.BicIbanService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BicIbanServiceTest {

    private final BicIbanService service = new BicIbanService();

    @Test
    void validGermanIban_passes() {
        var result = service.validateIban("DE89 3704 0044 0532 0130 00");
        assertTrue(result.valid());
        assertEquals("DE", result.country());
        assertTrue(result.sepa());
    }

    @Test
    void invalidCheckDigits_fail() {
        assertFalse(service.validateIban("DE89370400440532013001").valid());
    }

    @Test
    void validBic8_and_bic11() {
        assertTrue(service.validateBic("DEUTDEFF").valid());
        var bic11 = service.validateBic("DEUTDEFF500");
        assertTrue(bic11.valid());
        assertEquals("500", bic11.branch());
        assertEquals("DE", bic11.country());
    }

    @Test
    void invalidBic_fails() {
        assertFalse(service.validateBic("BAD").valid());
    }
}
