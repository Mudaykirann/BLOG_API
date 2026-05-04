package com.API.BlogV2.Service;

import com.API.BlogV2.Utils.ImageKitConfig;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageKitService {

    private final ImageKitConfig config;




    public ImageKitService(ImageKitConfig config) {
        this.config = config;
    }

    /**
     * Generates auth params for the frontend to upload directly to ImageKit.
     * Called BEFORE the frontend uploads — frontend sends these along with the image.
     */
    public Map<String, String> getAuthParams() {
        try {
            // 1. Generate a unique token
            String token = UUID.randomUUID().toString();

            // 2. Expire in 30 minutes
            String expire = String.valueOf((System.currentTimeMillis() / 1000) + 1800);

            // 3. Create HMAC-SHA1 signature: sign (token + expire) with private key
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(
                    config.getPrivateKey().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA1"
            );
            mac.init(keySpec);
            byte[] rawSignature = mac.doFinal((token + expire).getBytes(StandardCharsets.UTF_8));

            // 4. Convert to hex string
            String signature = HexFormat.of().formatHex(rawSignature);

            // 5. Return all 3 values — frontend needs all of them
            Map<String, String> authParams = new HashMap<>();
            authParams.put("token", token);
            authParams.put("expire", expire);
            authParams.put("signature", signature);
            authParams.put("publicKey", config.getPublicKey());       // handy for frontend
            authParams.put("urlEndpoint", config.getUrlEndpoint());   // handy for frontend

            return authParams;

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to generate ImageKit auth params", e);
        }
    }

    /**
     * Builds a transformation URL to resize/optimize images on the fly.
     * You can call this anytime to get a transformed version of a stored URL.
     *
     * Example: getTransformedUrl(imageUrl, 300, 300) →
     *   https://ik.imagekit.io/your_id/tr:w-300,h-300/image.jpg
     */
    public String getTransformedUrl(String imageUrl, int width, int height) {
        if (imageUrl == null || imageUrl.isEmpty()) return null;
        String endpoint = config.getUrlEndpoint();
        // Insert transformation after the endpoint
        String path = imageUrl.replace(endpoint, "");
        return endpoint + "/tr:w-" + width + ",h-" + height + path;
    }
}