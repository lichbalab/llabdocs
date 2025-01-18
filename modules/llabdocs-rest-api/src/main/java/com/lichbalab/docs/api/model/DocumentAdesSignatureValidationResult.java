package com.lichbalab.docs.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Result of document AdES signature validation")
public class DocumentAdesSignatureValidationResult extends CommonSignatureValidationResult {
    @Schema(description = "List of individual signature validation results")
    private List<AdesSignatureValidationResult> signatures;
    
    public List<AdesSignatureValidationResult> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<AdesSignatureValidationResult> signatures) {
        this.signatures = signatures;
    }
}
