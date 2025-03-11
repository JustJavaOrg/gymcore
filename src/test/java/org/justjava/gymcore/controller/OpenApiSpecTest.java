package org.justjava.gymcore.controller;

import de.cronn.validationfile.junit5.JUnit5ValidationFileAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OpenApiSpecTest implements JUnit5ValidationFileAssertions {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void verifyOpenApiSpec() {
        // Request the OpenAPI spec (adjust the endpoint if necessary)
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertEquals(200, response.getStatusCodeValue(), "Expected HTTP status 200 from the API docs endpoint");

        // Retrieve the spec and validate it using file-based assertions
        String openApiSpec = response.getBody();
        assertWithFile(openApiSpec);
    }
}