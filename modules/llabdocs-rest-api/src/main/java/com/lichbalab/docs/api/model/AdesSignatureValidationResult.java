package com.lichbalab.docs.api.model;

/// Sample of response in JSON format:
/// {
///   "signatureId" : "sdfsdfsdfsdfsdfsdfsdfsdf"
///   "indication": "TOTAL-FAILED",
///   "indicationDetails" : "The signature validation process results into INDETERMINATE because some constraints on the order"
///   "signer": "James John"
///   "validationTime": "2024-12-20T12:00:00Z",
///   "signingTime": "2024-12-19T12:00:00Z",
///   "signingCertificate": {
///       "subject": "CN=Invalid, O=FakeCorp, C=US",
///       "issuer": "CN=FakeCA, O=FakeCorp, C=US",
///       "serialNumber": "0000000000000000",
///       "validFrom": "2023-01-01T00:00:00Z",
///       "validTo": "2024-12-31T23:59:59Z",
///       "fingerprints": {
///         "SHA1": "00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00",
///         "SHA256": "0000000000000000000000000000000000000000000000000000000000000000"
///       }
///   },
///    "certificateChain": [
///       {
///         "subject": "CN=Example, OU=Development, O=ExampleCorp, L=City, ST=State, C=US",
///         "issuer": "CN=Example CA, O=ExampleCorp, C=US",
///         "serialNumber": "123456789ABCDEF"
///       },
///       {
///         "subject": "CN=Example CA, O=ExampleCorp, C=US",
///         "issuer": "CN=Root CA, O=RootCorp, C=US",
///         "serialNumber": "987654321FEDCBA"
///       }
///   ]
///   "signatureDetails": {
///     "errors": [
///       "Unable to verify certificate chain.",
///       "Certificate revoked by issuer."
///     ],
///     "warns" : [
///     ],
///     "infos" : [
///     ]
///   },
///   qualification : {
///     "level" : "QESig",
///     "description" : "Qualified Electronic Signature",
///     details: {
///       "errors": [
///       "Unable to verify certificate chain.",
///       "Certificate revoked by issuer."
///     ],
///     "warns" : [
///     ],
///     "infos" : [
///     ]
///     }
///   }
/// }

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema(description = "Detailed result of a AdES single signature validation")
public class AdesSignatureValidationResult {
    @Schema(description = "Unique identifier of the signature")
    private String signatureId;
    
    @Schema(description = "Overall indication of the signature validation (e.g., TOTAL-PASSED, TOTAL-FAILED)")
    private String indication;
    
    @Schema(description = "Detailed explanation of the validation indication")
    private String indicationDetails;
    
    @Schema(description = "Name or identifier of the signer")
    private String signer;
    
    @Schema(description = "Time when the signature was validated", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date validationTime;
    
    @Schema(description = "Time when the document was signed", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date signingTime;
    
    private CertificateInfo signingCertificate;
    private List<CertificateInfo> certificateChain;
    private ValidationDetails signatureDetails;

    // Nested Certificate Info class
    @Schema(description = "Information about a certificate")
    public static class CertificateInfo {
        @Schema(description = "Subject distinguished name")
        private String subject;
        
        @Schema(description = "Issuer distinguished name")
        private String issuer;
        
        @Schema(description = "Certificate serial number")
        private String serialNumber;
        
        @Schema(description = "Certificate validity start date", format = "date-time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private Date validFrom;
        
        @Schema(description = "Certificate validity end date", format = "date-time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private Date validTo;

        // Getters and Setters
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getIssuer() { return issuer; }
        public void setIssuer(String issuer) { this.issuer = issuer; }
        
        public String getSerialNumber() { return serialNumber; }
        public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
        
        public Date getValidFrom() { return validFrom; }
        public void setValidFrom(Date validFrom) { this.validFrom = validFrom; }
        
        public Date getValidTo() { return validTo; }
        public void setValidTo(Date validTo) { this.validTo = validTo; }
    }

    // Nested Validation Details class
    @Schema(description = "Details of the validation process")
    public static class ValidationDetails {
        @Schema(description = "List of validation errors")
        private List<String> errors;
        
        @Schema(description = "List of validation warnings")
        private List<String> warns;
        
        @Schema(description = "List of informational messages")
        private List<String> infos;

        // Getters and Setters
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        
        public List<String> getWarns() { return warns; }
        public void setWarns(List<String> warns) { this.warns = warns; }
        
        public List<String> getInfos() { return infos; }
        public void setInfos(List<String> infos) { this.infos = infos; }
    }

    // Getters and Setters for main class
    public String getSignatureId() { return signatureId; }
    public void setSignatureId(String signatureId) { this.signatureId = signatureId; }

    public String getIndication() { return indication; }
    public void setIndication(String indication) { this.indication = indication; }

    public String getIndicationDetails() { return indicationDetails; }
    public void setIndicationDetails(String indicationDetails) { this.indicationDetails = indicationDetails; }

    public String getSigner() { return signer; }
    public void setSigner(String signer) { this.signer = signer; }

    public Date getValidationTime() { return validationTime; }
    public void setValidationTime(Date validationTime) { this.validationTime = validationTime; }

    public Date getSigningTime() { return signingTime; }
    public void setSigningTime(Date signingTime) { this.signingTime = signingTime; }

    public CertificateInfo getSigningCertificate() { return signingCertificate; }
    public void setSigningCertificate(CertificateInfo signingCertificate) { this.signingCertificate = signingCertificate; }

    public List<CertificateInfo> getCertificateChain() { return certificateChain; }
    public void setCertificateChain(List<CertificateInfo> certificateChain) { this.certificateChain = certificateChain; }

    public ValidationDetails getSignatureDetails() { return signatureDetails; }
    public void setSignatureDetails(ValidationDetails signatureDetails) { this.signatureDetails = signatureDetails; }
}
