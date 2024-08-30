package com.lichbalab.docs.controller;

import com.lichbalab.docs.signature.DocSignService;
import com.lichbalab.docs.signature.SignatureValidationServiceLLab;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;

@RestController
@RequestMapping("/docs")
public class DocController {
    private final DocSignService docSignService;
    private final SignatureValidationServiceLLab signatureValidationServiceLLab;

    @Autowired
    public DocController(DocSignService docSignService, SignatureValidationServiceLLab signatureValidationServiceLLab) {
        this.docSignService = docSignService;
        this.signatureValidationServiceLLab = signatureValidationServiceLLab;
    }

    @PostMapping("/sign/pdf")
    public ResponseEntity<StreamingResponseBody> signPdf(@RequestParam("document") MultipartFile document, @RequestParam("alias") String certificateAlias) {

        StreamingResponseBody responseBody = outputStream -> {
            DSSDocument signedDoc = docSignService.signPdf(document.getInputStream(), certificateAlias);
            signedDoc.writeTo(outputStream);
            // Write data to the outputStream
            // This could be streaming file data, generating data on the fly, etc.
        };
        return ResponseEntity.ok()
                 .contentType(MediaType.APPLICATION_PDF)
                 .body(responseBody);
    }

    @PostMapping("/validate/signature")
    public ResponseEntity<WSReportsDTO> validateSignature(@RequestParam("document") MultipartFile document) throws IOException {
        WSReportsDTO reports = signatureValidationServiceLLab.validateSignature(document.getBytes());
        return ResponseEntity.ok(reports);
    }
}