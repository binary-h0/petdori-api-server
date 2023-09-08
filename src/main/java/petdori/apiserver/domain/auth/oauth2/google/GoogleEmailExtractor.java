package petdori.apiserver.domain.auth.oauth2.google;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleEmailExtractor {
    private final String GOOGLE_API_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private final RestTemplate restTemplate = new RestTemplate();
    private final JSONParser jsonParser = new JSONParser();

    public String extractEmailFromGoogleAccessToken(String accessToken) throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> res = restTemplate.exchange(
                GOOGLE_API_URL, HttpMethod.GET, httpEntity, String.class);

        JSONObject userInfo = (JSONObject) jsonParser.parse(res.getBody());
        return (String) userInfo.get("email");
    }
}
