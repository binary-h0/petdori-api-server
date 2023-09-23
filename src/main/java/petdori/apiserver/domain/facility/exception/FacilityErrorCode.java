package petdori.apiserver.domain.facility.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum FacilityErrorCode {
    FACILITY_TYPE_NOT_EXIST(HttpStatus.NOT_FOUND, "등록되지 않은 시설 종류입니다");

    private HttpStatus httpStatus;
    private String errorMessage;
}
