package com.lichbalab.docs.api.controller;

import com.lichbalab.docs.api.error.ErrorResponse;
import com.lichbalab.docs.api.mapper.ValidationResponseMapper;
import com.lichbalab.docs.api.model.DocumentSignatureValidationResult;
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

    @Operation(
            summary = "Validates Advanced Electronic Signatures (AdES) in a Document",
            description = """
                        This endpoint validates Advanced Electronic Signatures (AdES) in a document to ensure their authenticity, 
                        integrity, and compliance with standards like eIDAS. AdES includes various formats such as XAdES, CAdES, PAdES, and ASiC.  
                        The validation process includes:  
                    
                        1. Extracting the Signature: Identifies and extracts the AdES signature(s) and metadata from the document.  
                        2. Format Detection: Determines the specific AdES format (e.g., XAdES, CAdES, PAdES, or ASiC) and applies appropriate validation rules.  
                        3. Cryptographic Integrity Check: Verifies the signature's cryptographic validity, ensuring the data has not been altered.  
                        4. Certificate Validation:  
                           - Confirms the validity of certificates used to create the signature.  
                           - Checks for certificate revocation status using CRL (Certificate Revocation List) and OCSP (Online Certificate Status Protocol).  
                           - Ensures the certificates are issued by trusted Certificate Authorities (CAs).  
                    
                        This operation is crucial for verifying document authenticity, ensuring data integrity, and maintaining compliance with international digital signature standards.  
                    """,
            tags = {"Advanced Electronic Signature Validation"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Document validated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DocumentSignatureValidationResult.class)
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
    public ResponseEntity<DocumentSignatureValidationResult> validateAdesSignature(
            @RequestParam(value = "document")
            @Parameter(
                    description = "The document file containing  Advanced Electronic Signatures (AdES) to validate. ",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary"))
            )
            MultipartFile document,
            HttpServletRequest request) throws IOException {
        WSReportsDTO reports = signatureValidationServiceLLab.validateSignature(document.getBytes(), document.getOriginalFilename(), false);
        return ResponseEntity.ok(mapper.toValidationResult(reports, request.getLocale()));
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
                    """,
            tags = {"Qualified Electronic Signature Validation"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Document validated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DocumentSignatureValidationResult.class)
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
    public ResponseEntity<DocumentSignatureValidationResult> validateQesSignature(
            @RequestParam(value = "document")
            @Parameter(
                    description = "The document file containing Qualified Electronic Signatures (QES) to validate. " +
                            "The document should contain signatures created with qualified certificates " +
                            "from trusted Certificate Authorities (CAs).",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary"))
            ) MultipartFile document,
            HttpServletRequest request) throws IOException {
        WSReportsDTO reports = signatureValidationServiceLLab.validateSignature(document.getBytes(), document.getOriginalFilename(), true);
        return ResponseEntity.ok(mapper.toValidationResult(reports, request.getLocale()));
    }
}