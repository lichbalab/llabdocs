package com.lichbalab.docs.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Result of document signature validation")
public class DocumentSignatureValidationResult {
    @Schema(description = "Name of the validated document")
    private String documentName;
    
    @Schema(description = "List of individual signature validation results")
    private List<SignatureValidationResult> signatures;
    
    @Schema(description = "Summary of validation results")
    private ValidationSummary summary;

    @Schema(description = "Summary statistics of signature validation")
    public static class ValidationSummary {
        @Schema(description = "Total number of signatures in the document")
        private int totalSignatures;
        
        @Schema(description = "Number of valid signatures")
        private int validSignatures;

        public int getTotalSignatures() {
            return totalSignatures;
        }

        public void setTotalSignatures(int totalSignatures) {
            this.totalSignatures = totalSignatures;
        }

        public int getValidSignatures() {
            return validSignatures;
        }

        public void setValidSignatures(int validSignatures) {
            this.validSignatures = validSignatures;
        }
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public List<SignatureValidationResult> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<SignatureValidationResult> signatures) {
        this.signatures = signatures;
    }

    public ValidationSummary getSummary() {
        return summary;
    }

    public void setSummary(ValidationSummary summary) {
        this.summary = summary;
    }
}
