# petdori-api-server
소프트웨어 마에스트로 14기 솜사탕팀 - 펫도리 API 서버 레포지토리

## 실행 방법
- `applcation.properties` 작성 (or `appplication.yml`)
- 다음 내용 입력(여기선 `applcation.properties`이라고 가정)
```bash
spring.datasource.driver-class-name = com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://{DB호스트}:{DB포트}/{DB명}
spring.datasource.username={DB관리자계정}
spring.datasource.password={DB관리자비밀번호}

spring.jpa.hibernate.ddl-auto=none

jwt.access.secret={악세스비밀키}
jwt.refresh.secret={리프레시비밀키}

oauth2.apple.issuer={애플id토큰 공급자}
oauth2.apple.client_id={애플id토큰 대상자}

spring.data.redis.host={레디스 호스트}
spring.data.redis.port={레디스 포트}
```
- db username 등은 노션 참고

