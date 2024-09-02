package com.lichbalab.docs.controller;

import com.lichbalab.cmc.spring.sdk.CmcRestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TestCmcRestTemplateService implements CmcRestTemplate {

    @Override
    public RestTemplate getRestTemplate() {
        return null;
    }

    @Override
    public void updateSslBundle() {

    }
}
