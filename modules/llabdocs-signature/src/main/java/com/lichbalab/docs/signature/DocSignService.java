package com.lichbalab.docs.signature;

import java.io.IOException;
import java.io.InputStream;

import eu.europa.esig.dss.model.DSSDocument;

public interface DocSignService {
    /**
     *
     * @param doc The document to be signed
     * @param certAlias alias of Certificate for signature.
     * @return Signed document
     */
    DSSDocument signPdf(InputStream doc, String certAlias) throws IOException;

}
