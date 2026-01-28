package com.lichbalab.docs.api.error;

public class LLabException extends RuntimeException {
    private final LLabErrorCode errorCode;
    private final Object[] params;

    public LLabException(Throwable cause, LLabErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
        this.params = new Object[0];
    }

    public LLabException(LLabErrorCode errorCode, Object[] params) {
        this.errorCode = errorCode;
        this.params = params;
    }

    public LLabErrorCode getErrorCode() {
        return this.errorCode;
    }

    public Object[] getParams() {
        return this.params;
    }
}
