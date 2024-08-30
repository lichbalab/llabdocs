package com.lichbalab.docs.signature;

import java.security.PrivateKey;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.token.DSSPrivateKeyAccessEntry;

public class DssAccessEntryLLab implements DSSPrivateKeyAccessEntry {

    private final PrivateKey          privateKey;
    private final CertificateToken    certificateToken;
    private final CertificateToken[]  certificateChain;
    private final EncryptionAlgorithm encryptionAlgorithm;

    public DssAccessEntryLLab(Certificate certificate) {
        certificateToken = SignUtil.getDssCertificateToken(certificate);
        certificateChain = SignUtil.getDssCertificateChain(certificate).toArray(new CertificateToken[0]);
        encryptionAlgorithm = EncryptionAlgorithm.forKey(certificateToken.getPublicKey());
        privateKey = CertificateUtils.convertBytesToPrivateKey(certificate.getPrivateKeyData());
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public CertificateToken getCertificate() {
        return certificateToken;
    }

    @Override
    public CertificateToken[] getCertificateChain() {
        return certificateChain;
    }

    @Override
    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }
}