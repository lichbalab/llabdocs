package com.lichbalab.docs.api.controller;

import com.lichbalab.docs.api.mapper.ValidationResponseMapper;
import com.lichbalab.docs.api.model.DocumentSignatureValidationResult;
import com.lichbalab.docs.signature.DocSignService;
import com.lichbalab.docs.signature.SignatureValidationServiceLLab;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;

@RestController
@RequestMapping("/docs/validate")
public class DocValidationController {
    private final SignatureValidationServiceLLab signatureValidationServiceLLab;
    private final ValidationResponseMapper mapper;

    @Autowired
    public DocValidationController(SignatureValidationServiceLLab signatureValidationServiceLLab, ValidationResponseMapper mapper) {
        this.signatureValidationServiceLLab = signatureValidationServiceLLab;
        this.mapper = mapper;
    }

    @PostMapping("/signature")
    public ResponseEntity<DocumentSignatureValidationResult> validateSignature(@RequestParam("document") MultipartFile document, HttpServletRequest request) throws IOException {
        WSReportsDTO reports = signatureValidationServiceLLab.validateSignature(document.getBytes(), document.getOriginalFilename());
        return ResponseEntity.ok(mapper.toValidationResult(reports, request.getLocale()));
    }
}