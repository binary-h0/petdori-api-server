package petdori.apiserver.domain.facility.exception;

import lombok.Getter;

@Getter
public class FacilityException extends RuntimeException {
    private final FacilityErrorCode facilityErrorCode;

    public FacilityException(FacilityErrorCode facilityErrorCode) {
        super(facilityErrorCode.getErrorMessage());
        this.facilityErrorCode = facilityErrorCode;
    }
}
