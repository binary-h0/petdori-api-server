package petdori.apiserver.domain.auth.exception.dog;

public class InvalidDogGenderException extends DogException {
    public InvalidDogGenderException() {
        super(DogErrorCode.INVALID_DOG_GENDER);
    }
}
