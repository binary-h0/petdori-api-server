package petdori.apiserver.domain.dog.exception;

import lombok.Getter;

@Getter
public class DogException extends RuntimeException {
    private final DogErrorCode dogErrorCode;

    public DogException(DogErrorCode dogErrorCode) {
        super(dogErrorCode.getErrorMessage());
        this.dogErrorCode = dogErrorCode;
    }
}
