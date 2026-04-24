    package com.API.BlogV2.Controller;


    import com.API.BlogV2.DTO.TokenRequest;
    import com.API.BlogV2.Entity.RefreshToken;
    import com.API.BlogV2.Service.JWTService;
    import com.API.BlogV2.Service.RefreshTokenService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping(path = "api/v1")
    public class RefreshTokenController {

        @Autowired
        private JWTService jwtService;

        @Autowired
        private RefreshTokenService refreshTokenService;




        @PostMapping("/auth/refreshtoken")
        public ResponseEntity<?> refreshtoken(@RequestBody TokenRequest request) {
            String requestRefreshToken = request.getRefreshToken();

            return refreshTokenService.findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        // Generate a new short-lived Access Token
                        String newAccessToken = jwtService.generateToken(user.getName());

                        // Return both tokens to the client
                        return ResponseEntity.ok(new TokenResponse(newAccessToken, requestRefreshToken));
                    })
                    .orElseThrow(() -> new RuntimeException("Refresh token is not in database or expired!"));
        }
    }
