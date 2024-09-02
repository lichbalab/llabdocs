package com.lichbalab.docs.controller;

import com.lichbalab.cmc.spring.sdk.test.BaseIntegrationTest;
import com.lichbalab.cmc.spring.sdk.test.CertConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.server.Ssl;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DocsControllerIT extends BaseIntegrationTest {

    @LocalServerPort
    private Integer port;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @DynamicPropertySource
    public static void setCmcClientProperties(DynamicPropertyRegistry registry) {
        registry.add("TEST_CMC_API_PORT", () -> CMC_API.getMappedPort(API_EXPOSED_PORT));
        registry.add("TEST_CMC_CLIENT_AUTH", Ssl.ClientAuth.NONE::name);
        registry.add("TEST_CMC_CRON", () -> "0 0 0 * * * ");
        registry.add("CMC_CRON", () -> "0 0 0 * * * ");
        //registry.add("TEST_KEY_ALIAS", () -> CertConfig.ALIAS_2);
        registry.add("TEST_DISABLE_HOSTNAME_VERIFICATION", () -> "true");
        registry.add("CMC_API_BASE_URL", () -> "http://localhost:" + API_EXPOSED_PORT);
    }


    @Test
    void validateSignature() {
        File fileToUpload = new File("src/test/resources/docs/test_doc_signed.pdf");

        Response response = given().multiPart("document", fileToUpload).when().post("/docs/validate/signature");

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("application/json", response.getContentType());
        // Add more assertions based on expected response content
    }

    @Override
    protected int getPort() {
        return port;
    }
}