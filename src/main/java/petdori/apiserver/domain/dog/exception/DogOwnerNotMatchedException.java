package petdori.apiserver.domain.dog.exception;

public class DogOwnerNotMatchedException extends DogException {
    public DogOwnerNotMatchedException() {
        super(DogErrorCode.DOG_OWNER_NOT_MATCHED);
    }
}
