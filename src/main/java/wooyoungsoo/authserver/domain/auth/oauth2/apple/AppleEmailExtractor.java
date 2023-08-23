package wooyoungsoo.authserver.domain.auth.oauth2.apple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import java.security.PublicKey;

@Component
public class AppleEmailExtractor {
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final String issuer;
    private final String clientId;

    public AppleEmailExtractor(@Value("${oauth2.apple.issuer}") String issuer,
                               @Value("${oauth2.apple.client_id}") String clientId) {
        this.applePublicKeyGenerator = new ApplePublicKeyGenerator();
        this.issuer = issuer;
        this.clientId = clientId;
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
        if (!claims.getIssuer().equals(issuer)) {
            throw new RuntimeException("invalid issuer");
        }

        if (!claims.getAudience().equals(clientId)) {
            throw new RuntimeException("invalid audience");
        }
    }
}
