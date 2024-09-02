package com.lichbalab.docs.controller;

import com.lichbalab.cmc.spring.sdk.CmcSdkProperties;
import com.lichbalab.cmc.spring.sdk.CmcTomcatWebServerCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class TestCmcTomcatWebServerCustomizer extends CmcTomcatWebServerCustomizer {

    public TestCmcTomcatWebServerCustomizer() {
        super(new CmcSdkProperties());
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {

    }

}
