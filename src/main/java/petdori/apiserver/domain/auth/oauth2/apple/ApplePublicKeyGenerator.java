package petdori.apiserver.domain.auth.oauth2.apple;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import petdori.apiserver.domain.auth.exception.oauth2.ApplePublicKeyNotGenerateException;
import petdori.apiserver.domain.auth.exception.oauth2.AppleKeyInfoNotReceivedException;
import petdori.apiserver.domain.auth.exception.oauth2.MatchedKeyNotFoundException;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApplePublicKeyGenerator {
    private final String APPLE_PUBLIC_KEY_URL = "https://appleid.apple.com/auth/keys";
    private final RestTemplate restTemplate = new RestTemplate();
    private final JSONParser jsonParser = new JSONParser();

    public PublicKey generatePublicKeyMatchedWithIdToken(String idToken) throws ParseException {
        Map<String, String> algAndKid = getAlgAndKidFromIdToken(idToken);
        JSONArray availablePublicKeyObjects = getAvailablePublicKeyObjects();
        JSONObject matchedPublicKeyObj = findMatchedPublicKeyObj(
                availablePublicKeyObjects, algAndKid.get("alg"), algAndKid.get("kid"));
        return generatePublicKey(matchedPublicKeyObj);
    }

    private Map<String, String> getAlgAndKidFromIdToken(String idToken) throws ParseException {
        Map<String, String> algAndKid = new HashMap<>();

        String header = idToken.split("\\.")[0];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        JSONObject headerContent = (JSONObject) jsonParser.parse(
                new String(decoder.decode(header))
        );

        algAndKid.put("alg", (String) headerContent.get("alg"));
        algAndKid.put("kid", (String) headerContent.get("kid"));

        return algAndKid;
    }

    private JSONArray getAvailablePublicKeyObjects() throws ParseException {
        HttpEntity<String> httpEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<String> res = restTemplate.exchange(
                APPLE_PUBLIC_KEY_URL, HttpMethod.GET, httpEntity, String.class);
        JSONObject availablePublicKeysContent = (JSONObject) jsonParser.parse(res.getBody());
        return (JSONArray) availablePublicKeysContent.get("keys");
    }

    private JSONObject findMatchedPublicKeyObj(JSONArray availablePublicKeyObjects, String alg, String kid) {
        if (availablePublicKeyObjects == null || availablePublicKeyObjects.isEmpty()) {
            throw new AppleKeyInfoNotReceivedException();
        }

        for (JSONObject keyObj : (Iterable<JSONObject>) availablePublicKeyObjects) {
            String algFromKey = (String) keyObj.get("alg");
            String kidFromKey = (String) keyObj.get("kid");

            if (Objects.equals(algFromKey, alg) && Objects.equals(kidFromKey, kid)) {
                return keyObj;
            }
        }

        return null;
    }

    private PublicKey generatePublicKey(JSONObject applePublicKeyObj) {
        if (applePublicKeyObj == null) {
            throw new MatchedKeyNotFoundException();
        }

        String kty = (String) applePublicKeyObj.get("kty");
        byte[] modulusBytes = Base64.getUrlDecoder().decode((String) applePublicKeyObj.get("n"));
        byte[] exponentBytes = Base64.getUrlDecoder().decode((String) applePublicKeyObj.get("e"));

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                new BigInteger(1, modulusBytes),
                new BigInteger(1, exponentBytes)
        );

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(kty);
             return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ApplePublicKeyNotGenerateException();
        }
    }
}
