package petdori.apiserver.domain.dog.exception;

public class InvalidDogGenderException extends DogException {
    public InvalidDogGenderException() {
        super(DogErrorCode.INVALID_DOG_GENDER);
    }
}
