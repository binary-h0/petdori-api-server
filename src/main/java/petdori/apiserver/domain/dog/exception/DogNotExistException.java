package petdori.apiserver.domain.dog.exception;

public class DogNotExistException extends DogException {
    public DogNotExistException() {
        super(DogErrorCode.DOG_NOT_EXIST);
    }
}
