package petdori.apiserver.domain.dog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum DogErrorCode {
    DOG_TYPE_NOT_EXIST(HttpStatus.NOT_FOUND, "등록되지 않은 견종입니다"),
    INVALID_DOG_GENDER(HttpStatus.NOT_FOUND, "성별 이름은 암컷 또는 수컷만 가능합니다"),
    DOG_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 강아지가 존재하지 않습니다"),
    DOG_OWNER_NOT_MATCHED(HttpStatus.FORBIDDEN, "해당 강아지의 주인이 아닙니다");

    private HttpStatus httpStatus;
    private String errorMessage;
}
