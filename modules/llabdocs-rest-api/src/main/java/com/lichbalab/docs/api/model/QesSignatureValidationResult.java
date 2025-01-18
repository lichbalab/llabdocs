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

@Schema(description = "Detailed result of a QES single signature validation")
public class QesSignatureValidationResult extends AdesSignatureValidationResult {
    private QualificationInfo qualification;

    // Nested Qualification Info class
    @Schema(description = "Information about signature qualification")
    public static class QualificationInfo {
        @Schema(description = "Qualification level (e.g., QESig)")
        private String level;
        
        @Schema(description = "Human-readable description of the qualification")
        private String description;
        
        @Schema(description = "Detailed validation information for qualification")
        private ValidationDetails details;

        // Getters and Setters
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public ValidationDetails getDetails() { return details; }
        public void setDetails(ValidationDetails details) { this.details = details; }
    }

    // Getters and Setters for main class
    public QualificationInfo getQualification() { return qualification; }
    public void setQualification(QualificationInfo qualification) { this.qualification = qualification; }
}
