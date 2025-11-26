package com.clashofserres.cinematch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tmdb.api")
public class TmdbConfig {

    private String key;
    private String token;
    private String baseUrl;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBaseUrl() {
        return baseUrl;

    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}

