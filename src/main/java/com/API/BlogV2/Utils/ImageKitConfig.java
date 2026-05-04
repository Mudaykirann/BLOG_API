// Utils/ImageKitConfig.java
package com.API.BlogV2.Utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "imagekit")   // KEEP this
// ❌ REMOVE @Configuration from here
public class ImageKitConfig {

    private String publicKey;
    private String privateKey;
    private String urlEndpoint;

    // Getters and Setters
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getPrivateKey() { return privateKey; }
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }

    public String getUrlEndpoint() { return urlEndpoint; }
    public void setUrlEndpoint(String urlEndpoint) { this.urlEndpoint = urlEndpoint; }
}