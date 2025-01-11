package com.lichbalab.docs.controller;

import com.lichbalab.cmc.spring.sdk.test.BaseIntegrationTest;
import com.lichbalab.docs.api.model.DocumentSignatureValidationResult;
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
        String documentName = "test_doc_signed.pdf";
        File fileToUpload = new File("src/test/resources/docs/" + documentName);

        Response response = given().multiPart("document", fileToUpload).when().post("/docs/validate/signature");
        DocumentSignatureValidationResult result = response.as(DocumentSignatureValidationResult.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertEquals(documentName, result.getDocumentName());
    }

    @Test
    void validateCorruptedSignature() {
        String documentName = "pdf-signed-corrupted.pdf";
        File fileToUpload = new File("src/test/resources/docs/" + documentName);

        Response response = given().multiPart("document", fileToUpload).when().post("/docs/validate/signature");
        DocumentSignatureValidationResult result = response.as(DocumentSignatureValidationResult.class);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertEquals(documentName, result.getDocumentName());
        
        // Verify that validation detected issues
        Assertions.assertNotNull(result.getSummary());
        Assertions.assertTrue(result.getSummary().getValidSignatures() < result.getSummary().getTotalSignatures(),
                "Expected some signatures to be invalid in corrupted document");
        
        // Verify that at least one signature has validation errors
        Assertions.assertTrue(result.getSignatures().stream()
                .anyMatch(sig -> sig.getIndication() != null && sig.getIndication().equals("TOTAL_FAILED")),
                "Expected at least one signature to have validation issues");
    }

    @Override
    protected int getPort() {
        return port;
    }
}