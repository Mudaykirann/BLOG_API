package com.API.BlogV2.Controller;

import com.API.BlogV2.Service.ImageKitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping(path="/api/v1/imagekit")
public class ImageKitController {

    private final ImageKitService imageKitService;

    public ImageKitController(ImageKitService imageKitService) {
        this.imageKitService = imageKitService;
    }

    /**
     * GET /api/imagekit/auth
     * Frontend calls this before uploading an image.
     * Returns: { token, expire, signature, publicKey, urlEndpoint }
     */
    @GetMapping("/auth")
    public ResponseEntity<Map<String, String>> getAuthParams() {
        return ResponseEntity.ok(imageKitService.getAuthParams());
    }
}