package com.lichbalab.docs.api.error;

public enum LLabErrorCode {
    GENERAL(500),
    TOO_MANY_VALIDATION_REQUESTS(429);

    final int status;

    private LLabErrorCode(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}