package com.lichbalab.docs.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Result of document QES signature validation")
public class DocumentQesSignatureValidationResult extends CommonSignatureValidationResult {
    @Schema(description = "List of individual signature validation results")
    private List<QesSignatureValidationResult> signatures;
    
    public List<QesSignatureValidationResult> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<QesSignatureValidationResult> signatures) {
        this.signatures = signatures;
    }
}
