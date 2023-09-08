package petdori.apiserver.domain.auth.oauth2.kakao;

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
public class KakaoEmailExtractor {
    private final String KAKAO_API_URL = "https://kapi.kakao.com/v2/user/me";
    private final RestTemplate restTemplate = new RestTemplate();
    private final JSONParser jsonParser = new JSONParser();

    public String extractEmailFromKakaoAccessToken(String accessToken) throws ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> res = restTemplate.exchange(KAKAO_API_URL, HttpMethod.GET, httpEntity, String.class);

        JSONObject userInfo = (JSONObject) jsonParser.parse(res.getBody());
        JSONObject kakaoAccountInfo = (JSONObject) userInfo.get("kakao_account");

        return (String) kakaoAccountInfo.get("email");
    }
}
