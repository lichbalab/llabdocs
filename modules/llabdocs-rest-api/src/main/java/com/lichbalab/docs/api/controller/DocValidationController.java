package com.lichbalab.docs.api.controller;

import com.lichbalab.docs.api.error.ErrorResponse;
import com.lichbalab.docs.api.mapper.ValidationResponseMapper;
import com.lichbalab.docs.api.model.DocumentAdesSignatureValidationResult;
import com.lichbalab.docs.api.model.DocumentQesSignatureValidationResult;
import com.lichbalab.docs.signature.SignatureValidationServiceLLab;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/docs/validate")
@PreAuthorize("isAuthenticated()")
public class DocValidationController {
    private final SignatureValidationServiceLLab signatureValidationServiceLLab;
    private final ValidationResponseMapper mapper;

    @Autowired
    public DocValidationController(SignatureValidationServiceLLab signatureValidationServiceLLab, ValidationResponseMapper mapper) {
        this.signatureValidationServiceLLab = signatureValidationServiceLLab;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Validates Advanced Electronic Signatures (AdES) in a Document",
            description = """
                        This endpoint validates Advanced Electronic Signatures (AdES) in a document to ensure their authenticity, 
                        integrity, and compliance with standards like eIDAS.
                        The validation process includes:  
                    
                        1. Extracting the Signature: Identifies and extracts the AdES signature(s) and metadata from the document.  
                        2. Format Detection: Determines the specific AdES format (e.g., XAdES, CAdES, PAdES, or ASiC) and applies appropriate validation rules.  
                        3. Cryptographic Integrity Check: Verifies the signature's cryptographic validity, ensuring the data has not been altered.  
                        4. Certificate Validation:  
                           - Confirms the validity of certificates used to create the signature.  
                           - Checks for certificate revocation status using CRL (Certificate Revocation List) and OCSP (Online Certificate Status Protocol).  
                           - Ensures the certificates are issued by trusted Certificate Authorities (CAs).  
                    
                        The method supports signature validation for the following document formats: PDF, XML.                      
                    """,
            tags = {"Advanced Electronic Signature Validation"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Document validated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DocumentAdesSignatureValidationResult.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Any internal error during document validation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping(value = "/ades-signatures", consumes = "multipart/form-data")
    public ResponseEntity<DocumentAdesSignatureValidationResult> validateAdesSignature(
            @RequestParam(value = "document")
            @Parameter(
                    description = "The document file (PDF, XML) containing  Advanced Electronic Signatures (AdES) to validate. ",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary"))
            )
            MultipartFile document,
            HttpServletRequest request) throws IOException {
        WSReportsDTO reports = signatureValidationServiceLLab.validateAdesSignature(document.getBytes(), document.getOriginalFilename());
        return ResponseEntity.ok(mapper.toAdesValidationResult(reports, request.getLocale()));
    }

    @Operation(
            summary = "Validates Qualified Electronic Signatures (QES) in a Document",
            description = """
                        This endpoint validates Qualified Electronic Signatures (QES) in a document to ensure their authenticity, 
                        compliance, and legal validity under regulations such as eIDAS. The validation process includes:
                    
                        1. Extracting the Signature: Identifies and extracts the signature and its associated metadata from the document.
                        2. Cryptographic Integrity Check: Ensures that the signature's cryptographic mechanisms (e.g., hash, encryption) are valid and unaltered.
                        3. Certificate Qualification Validation:
                           - Confirms that the certificate used to create the signature is a Qualified Certificate for Electronic Signatures (QC).
                           - Checks the certificate's validity, including expiration, revocation status (via CRL and OCSP), and compliance with trusted root CAs.
                           - Verifies the certificate's qualification status using extensions and identifiers defined in standards such as ETSI EN 319 412.
                           - Ensures that the issuing Certificate Authority (CA) is on an officially trusted list (e.g., EU Trusted List).
                        4. Regulatory Compliance Check: Verifies that the signature fulfills all legal and technical requirements for QES as per eIDAS.        
                     
                        The method supports signature validation for the following document formats: PDF, XML.                      
                    """,
            tags = {"Qualified Electronic Signature Validation"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Document validated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DocumentQesSignatureValidationResult.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Any internal error during document validation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping(value = "/qes-signatures", consumes = "multipart/form-data")
    public ResponseEntity<DocumentQesSignatureValidationResult> validateQesSignature(
            @RequestParam(value = "document")
            @Parameter(
                    description = "The document file (PDF, XML) containing Qualified Electronic Signatures (QES) to validate. " +
                            "The document should contain signatures created with qualified certificates " +
                            "from trusted Certificate Authorities (CAs).",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary"))
            ) MultipartFile document,
            HttpServletRequest request) throws IOException {
        WSReportsDTO reports = signatureValidationServiceLLab.validateQesSignature(document.getBytes(), document.getOriginalFilename());
        return ResponseEntity.ok(mapper.toQesValidationResult(reports, request.getLocale()));
    }
}
