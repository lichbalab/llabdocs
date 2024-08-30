package com.lichbalab.docs.signature;

import java.util.Collections;
import java.util.List;

import com.lichbalab.certificate.Certificate;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.token.AbstractSignatureTokenConnection;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;

public class SignatureTokenLLab extends AbstractSignatureTokenConnection {

    private final Certificate certificate;


    public SignatureTokenLLab(Certificate certificate) {
        this.certificate = certificate;
    }

    @Override
    public void close() {

    }

    @Override
    public List<DSSPrivateKeyEntry> getKeys() throws DSSException {
        return Collections.singletonList(new DssAccessEntryLLab(certificate));
    }
}
