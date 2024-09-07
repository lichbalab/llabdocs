package com.lichbalab.docs.controller;

import com.lichbalab.cmc.spring.sdk.test.BaseIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

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

    @Test
    void validateSignature() {
        File fileToUpload = new File("src/test/resources/docs/test_doc_signed.pdf");

        Response response = given().multiPart("document", fileToUpload).when().post("/docs/validate/signature");

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("application/json", response.getContentType());
    }

    @Override
    protected int getPort() {
        return port;
    }
}