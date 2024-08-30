package com.lichbalab.docs.signature;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import com.lichbalab.certificate.Certificate;
import eu.europa.esig.dss.model.x509.CertificateToken;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;

public class SignUtil {

    public static CertificateToken getDssCertificateToken(Certificate certificate) {
        return getFromCertHolder(certificate.getCertChain().get(0));
    }

    public static List<CertificateToken> getDssCertificateChain(Certificate certificate) {
        return certificate.getCertChain().stream().map(SignUtil::getFromCertHolder).toList();
    }

    private static CertificateToken getFromCertHolder(X509CertificateHolder certificateHolder) {
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter();

        try {
            X509Certificate x509Certificate = converter.getCertificate(certificateHolder);
            return new CertificateToken(x509Certificate);
        } catch (CertificateException ex) {
            throw new RuntimeException(ex);
        }
    }
}