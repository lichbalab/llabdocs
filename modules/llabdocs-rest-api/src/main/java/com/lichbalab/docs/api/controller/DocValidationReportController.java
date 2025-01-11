package com.lichbalab.docs.api.controller;

import com.lichbalab.docs.signature.DocSignService;
import com.lichbalab.docs.signature.SignatureValidationServiceLLab;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/docs/validate/report")
public class DocValidationReportController {
    private final SignatureValidationServiceLLab signatureValidationServiceLLab;

    @Autowired
    public DocValidationReportController(SignatureValidationServiceLLab signatureValidationServiceLLab) {
        this.signatureValidationServiceLLab = signatureValidationServiceLLab;
    }

    @PostMapping("/html")
    public ResponseEntity<String> validateSignatureReportHtml(@RequestParam("document") MultipartFile document) throws IOException {
        String report = signatureValidationServiceLLab.validateSignatureSimpleHtmlReport(document.getBytes(), document.getOriginalFilename());
        return ResponseEntity.ok(report);
    }

}