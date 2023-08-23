package wooyoungsoo.authserver.domain.auth.oauth2.apple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AppleEmailExtractor {
    private final ApplePublicKeyGenerator applePublicKeyGenerator;

    public AppleEmailExtractor() {
        this.applePublicKeyGenerator = new ApplePublicKeyGenerator();
    }

    public String extractEmailFromAppleIdToken(String idToken) throws ParseException {
        PublicKey applePublicKey = applePublicKeyGenerator.
                generatePublicKeyMatchedWithIdToken(idToken);
        Claims claims = getClaimsFromIdToken(idToken, applePublicKey);
        verifyAppleIdTokenClaim(claims);

        return claims.get("email").toString();
    }

    public Claims getClaimsFromIdToken(String idToken, PublicKey publicKey) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(idToken)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void verifyAppleIdTokenClaim(Claims claims) {
        if (!claims.getIssuer().equals("https://appleid.apple.com")) {
            throw new RuntimeException("invalid issuer");
        }

        if (!claims.getAudience().equals("com.example.wooyoungsoo")) {
            throw new RuntimeException("invalid audience");
        }
    }
}
