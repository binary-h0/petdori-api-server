package petdori.apiserver.domain.facility.exception;

import lombok.Getter;

@Getter
public class FacilityTypeNotExistException extends FacilityException {
    private String typeName;

    public FacilityTypeNotExistException(String typeName) {
        super(FacilityErrorCode.FACILITY_TYPE_NOT_EXIST);
        this.typeName = typeName;
    }
}
