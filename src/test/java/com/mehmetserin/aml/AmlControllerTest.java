package com.mehmetserin.aml;

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
class AmlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void screen_endpoint() throws Exception {
        mockMvc.perform(post("/api/aml/screen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Maria Santos\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("BLOCK"));
    }
}
