package com.lichbalab.docs.signature;

import eu.europa.esig.dss.alert.ExceptionOnStatusAlert;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.AIASource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.spi.x509.revocation.crl.CRLSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.tsl.function.OfficialJournalSchemeInformationURI;
import eu.europa.esig.dss.tsl.function.TypeOtherTSLPointer;
import eu.europa.esig.dss.tsl.function.XMLOtherTSLPointer;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.sha2.Sha2FileCacheDataLoader;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.ws.validation.common.RemoteDocumentValidationService;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Import({SchedulingConfig.class })

public class LLabDocsConfig {

    @Value("${trusted.source.keystore.type:}")
    private String trustSourceKsType;

    @Value("${trusted.source.keystore.filename:}")
    private String trustSourceKsFilename;

    @Value("${trusted.source.keystore.password:}")
    private String trustSourceKsPassword;

    @Value("${cache.ocsp.max.next.update:0}")
    private long ocspMaxNextUpdate;

    @Value("${dataloader.connection.timeout}")
    private int connectionTimeout;

    @Value("${dataloader.connection.request.timeout}")
    private int connectionRequestTimeout;

    @Value("${dataloader.redirect.enabled}")
    private boolean redirectEnabled;

    @Value("${dataloader.use.system.properties}")
    private boolean useSystemProperties;

    @Autowired(required = false)
    private ProxyConfig proxyConfig;

    @Value("${cache.expiration:0}")
    private long cacheExpiration;

    @Value("${cache.crl.max.next.update:0}")
    private long crlMaxNextUpdate;

    @Value("${tl.loader.ades.enabled}")
    private boolean adesLotlEnabled;

    @Value("${tl.loader.ades.lotlUrl}")
    private String adesLotlUrl;

    @Value("${current.lotl.url}")
    private String lotlUrl;

    @Value("${tl.loader.trust.all}")
    private boolean tlTrustAllStrategy;

    @Value("${tl.loader.ades.keystore.type}")
    private String adesKeyStoreType;

    @Value("${tl.loader.ades.keystore.filename}")
    private String adesKeyStoreFilename;

    @Value("${tl.loader.ades.keystore.password}")
    private String adesKeyStorePassword;

    @Value("${tl.loader.ades.tsl.type}")
    private String adesTSLType;

    @Value("${oj.content.keystore.type}")
    private String ksType;

    @Value("${oj.content.keystore.filename}")
    private String ksFilename;

    @Value("${oj.content.keystore.password}")
    private String ksPassword;

    @Value("${current.oj.url}")
    private String currentOjUrl;


    @Bean
    public CertificateVerifier certificateVerifier() {
        CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier();
        certificateVerifier.setCrlSource(cachedCRLSource());
        certificateVerifier.setOcspSource(cachedOCSPSource());
        certificateVerifier.setAIASource(cachedAIASource());
        certificateVerifier.setTrustedCertSources(trustedListSource(), trustedCertificateSource());

        // Default configs
        certificateVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());
        certificateVerifier.setCheckRevocationForUntrustedChains(false);

        return certificateVerifier;
    }

    @Bean
    public OnlineCRLSource onlineCRLSource() {
        OnlineCRLSource onlineCRLSource = new OnlineCRLSource();
        onlineCRLSource.setDataLoader(dataLoader());
        return onlineCRLSource;
    }

    @Bean
    public CRLSource cachedCRLSource() {
        OnlineCRLSource onlineCRLSource = onlineCRLSource();
        FileCacheDataLoader fileCacheDataLoader = initFileCacheDataLoader();
        fileCacheDataLoader.setCacheExpirationTime(crlMaxNextUpdate * 1000); // to millis
        onlineCRLSource.setDataLoader(fileCacheDataLoader);
        return onlineCRLSource;
    }

    @Bean
    public OCSPSource cachedOCSPSource() {
        OnlineOCSPSource onlineOCSPSource = onlineOCSPSource();
        FileCacheDataLoader fileCacheDataLoader = initFileCacheDataLoader();
        fileCacheDataLoader.setDataLoader(ocspDataLoader());
        fileCacheDataLoader.setCacheExpirationTime(ocspMaxNextUpdate * 1000); // to millis
        onlineOCSPSource.setDataLoader(fileCacheDataLoader);
        return onlineOCSPSource;
    }

    @Bean
    public OnlineOCSPSource onlineOCSPSource() {
        OnlineOCSPSource onlineOCSPSource = new OnlineOCSPSource();
        onlineOCSPSource.setDataLoader(ocspDataLoader());
        return onlineOCSPSource;
    }

    @Bean
    public OCSPDataLoader ocspDataLoader() {
        return configureCommonsDataLoader(new OCSPDataLoader());
    }


    @Bean
    public FileCacheDataLoader fileCacheDataLoader() {
        FileCacheDataLoader fileCacheDataLoader = initFileCacheDataLoader();
        fileCacheDataLoader.setCacheExpirationTime(cacheExpiration * 1000); // to millis
        return fileCacheDataLoader;
    }

    @Bean
    public CommonsDataLoader dataLoader() {
        return configureCommonsDataLoader(new CommonsDataLoader());
    }

    @Bean
    public DSSFileLoader offlineLoader() {
        FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
        offlineFileLoader.setCacheExpirationTime(-1);
        offlineFileLoader.setDataLoader(new IgnoreDataLoader());
        offlineFileLoader.setFileCacheDirectory(tlCacheDirectory());
        return offlineFileLoader;
    }




    @Bean
    public AIASource cachedAIASource() {
        FileCacheDataLoader fileCacheDataLoader = fileCacheDataLoader();
        return new DefaultAIASource(fileCacheDataLoader);
    }

    @Bean
    public TLValidationJob job() {
        TLValidationJob job = new TLValidationJob();
        job.setTrustedListCertificateSource(trustedListSource());
        job.setListOfTrustedListSources(listOfTrustedListSources());
        job.setOfflineDataLoader(offlineLoader());
        job.setOnlineDataLoader(onlineLoader());
        return job;
    }

    @Bean
    public KeyStoreCertificateSource ojContentKeyStore() {
        try {
            return new KeyStoreCertificateSource(new File(ksFilename), ksType, ksPassword.toCharArray());
        } catch (IOException e) {
            throw new DSSException("Unable to load the file " + ksFilename, e);
        }
    }

    @Bean(name = "european-lotl-source")
    public LOTLSource europeanLOTL() {
        LOTLSource lotlSource = new LOTLSource();
        lotlSource.setUrl(lotlUrl);
        lotlSource.setCertificateSource(ojContentKeyStore());
        lotlSource.setSigningCertificatesAnnouncementPredicate(new OfficialJournalSchemeInformationURI(currentOjUrl));
        lotlSource.setPivotSupport(true);
        return lotlSource;
    }

    @Bean
    public KeyStoreCertificateSource adesLotlKeyStore() {
        try {
            return new KeyStoreCertificateSource(new File(adesKeyStoreFilename), adesKeyStoreType, adesKeyStorePassword.toCharArray());
        } catch (IOException e) {
            throw new DSSException("Unable to load the file " + adesKeyStoreFilename, e);
        }
    }

    @Bean(name = "ades-source")
    public LOTLSource adesLOTL() {
        LOTLSource adesLOTL = new LOTLSource();
        adesLOTL.setUrl(adesLotlUrl);
        adesLOTL.setCertificateSource(adesLotlKeyStore());
        adesLOTL.setMraSupport(true);
        adesLOTL.setPivotSupport(false);

        adesLOTL.setLotlPredicate(new XMLOtherTSLPointer().and(new TypeOtherTSLPointer(adesTSLType)));
        adesLOTL.setTlPredicate(new XMLOtherTSLPointer().and(new TypeOtherTSLPointer(adesTSLType)).negate()); // allow all TSL Types

        return adesLOTL;
    }

    @Bean
    public File tlCacheDirectory() {
        File rootFolder = new File(System.getProperty("java.io.tmpdir"));
        File tslCache = new File(rootFolder, "dss-tsl-loader");
        if (tslCache.mkdirs()) {
            //LOG.info("TL Cache folder : {}", tslCache.getAbsolutePath());
        }
        return tslCache;
    }

    @Bean
    public DSSFileLoader onlineLoader() {
        FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader();
        onlineFileLoader.setCacheExpirationTime(-1);
        onlineFileLoader.setDataLoader(tlDataLoader());
        onlineFileLoader.setFileCacheDirectory(tlCacheDirectory());
        return Sha2FileCacheDataLoader.initSha2DailyUpdateDataLoader(onlineFileLoader);
    }

    @Bean
    public CommonsDataLoader trustAllDataLoader() {
        CommonsDataLoader trustAllDataLoader = configureCommonsDataLoader(new CommonsDataLoader());
        trustAllDataLoader.setTrustStrategy(TrustAllStrategy.INSTANCE);
        return trustAllDataLoader;
    }

    @Bean
    public CommonsDataLoader tlDataLoader() {
        if (tlTrustAllStrategy) {
            //LOG.info("TrustAllStrategy is enabled on TL loading.");
            return trustAllDataLoader();
        } else {
            return dataLoader();
        }
    }

    @Bean(name = "european-trusted-list-certificate-source")
    public TrustedListsCertificateSource trustedListSource() {
        return new TrustedListsCertificateSource();
    }


    @Bean
    public RemoteDocumentValidationService remoteValidationService() {
        RemoteDocumentValidationService service = new RemoteDocumentValidationService();
        service.setVerifier(certificateVerifier());
/*
        if (defaultPolicy() != null) {
            try (InputStream is = defaultPolicy().getInputStream()) {
                service.setDefaultValidationPolicy(is);
            } catch (IOException e) {
                LOG.error(String.format("Unable to parse policy: %s", e.getMessage()), e);
            }
        }
*/
        return service;
    }

    public CommonTrustedCertificateSource trustedCertificateSource() {
        CommonTrustedCertificateSource trustedCertificateSource = new CommonTrustedCertificateSource();
        if (Utils.isStringNotEmpty(trustSourceKsFilename)) {
            try {
                KeyStoreCertificateSource keyStore = new KeyStoreCertificateSource(
                        new File(trustSourceKsFilename), trustSourceKsType, trustSourceKsPassword.toCharArray());
                trustedCertificateSource.importAsTrusted(keyStore);
            } catch (IOException e) {
                throw new DSSException("Unable to load the file " + trustSourceKsFilename, e);
            }
        }
        return trustedCertificateSource;
    }

    private LOTLSource[] listOfTrustedListSources() {
        List<LOTLSource> lotlSourceList = new ArrayList<>();
        lotlSourceList.add(europeanLOTL());
        if (adesLotlEnabled) {
            lotlSourceList.add(adesLOTL());
        }
        return lotlSourceList.toArray(new LOTLSource[0]);
    }

    private FileCacheDataLoader initFileCacheDataLoader() {
        FileCacheDataLoader fileCacheDataLoader = new FileCacheDataLoader();
        fileCacheDataLoader.setDataLoader(dataLoader());
        // Per default uses "java.io.tmpdir" property
        // fileCacheDataLoader.setFileCacheDirectory(new File("/tmp"));
        return fileCacheDataLoader;
    }

    private <C extends CommonsDataLoader> C configureCommonsDataLoader(C dataLoader) {
        dataLoader.setTimeoutConnection(connectionTimeout);
        dataLoader.setTimeoutConnectionRequest(connectionRequestTimeout);
        dataLoader.setRedirectsEnabled(redirectEnabled);
        dataLoader.setUseSystemProperties(useSystemProperties);
        dataLoader.setProxyConfig(proxyConfig);
        return dataLoader;
    }
}