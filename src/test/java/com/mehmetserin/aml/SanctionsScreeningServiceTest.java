package com.mehmetserin.aml;

import com.mehmetserin.aml.service.SanctionsScreeningService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SanctionsScreeningServiceTest {

    private final SanctionsScreeningService service = new SanctionsScreeningService();

    @Test
    void exactHit_blocks() {
        var result = service.screen("Ivan Petrov");
        assertEquals("BLOCK", result.decision());
        assertFalse(result.hits().isEmpty());
    }

    @Test
    void closeSpelling_reviewsOrBlocks() {
        var result = service.screen("Iwan Petrov");
        assertTrue(result.riskScore() >= 72);
        assertTrue(result.decision().equals("REVIEW") || result.decision().equals("BLOCK"));
    }

    @Test
    void cleanName_clears() {
        var result = service.screen("Ordinary Customer");
        assertEquals("CLEAR", result.decision());
        assertTrue(result.hits().isEmpty());
    }

    @Test
    void levenshtein_basic() {
        assertEquals(0, SanctionsScreeningService.levenshtein("abc", "abc"));
        assertEquals(1, SanctionsScreeningService.levenshtein("abc", "abx"));
    }
}
