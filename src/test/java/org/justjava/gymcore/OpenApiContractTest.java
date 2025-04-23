package org.justjava.gymcore;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OpenApiContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void generatedOpenApiMustMatchGoldenFile() throws Exception {
        // 1) Fetch the live spec
        String generated = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 2) Load the golden file from src/test/resources/open-api.json
        String golden = Files.readString(
                Paths.get("src/test/resources/open-api.json"),
                StandardCharsets.UTF_8
        );

        // 3) Strictly compare JSON structures (order and values must match)
        JSONAssert.assertEquals(golden, generated, /* strict = */ true);
    }
}
