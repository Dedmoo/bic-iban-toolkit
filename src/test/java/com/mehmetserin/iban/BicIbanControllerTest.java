package com.mehmetserin.iban;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BicIbanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validateIban_endpoint() throws Exception {
        mockMvc.perform(post("/api/iban/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":\"GB82WEST12345698765432\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.country").value("GB"));
    }
}
