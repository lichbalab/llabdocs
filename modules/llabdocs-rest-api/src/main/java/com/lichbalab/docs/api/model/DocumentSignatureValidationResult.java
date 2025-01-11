package com.lichbalab.docs.api.model;

import java.util.List;

public class DocumentSignatureValidationResult {
    private String documentName;
    private List<SignatureValidationResult> signatures;
    private ValidationSummary summary;

    public static class ValidationSummary {
        private int totalSignatures;
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
