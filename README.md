# wooyoungsoo-auth-server
소프트웨어 마에스트로 14기 솜사탕팀 - 우.영.수(우리집 영양 수의사) 로그인/회원가입/인가인증 관련 레포지토리

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

```
- db username 등은 노션 참고

