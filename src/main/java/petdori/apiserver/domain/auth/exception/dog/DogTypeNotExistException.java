package petdori.apiserver.domain.auth.exception.dog;

import lombok.Getter;

@Getter
public class DogTypeNotExistException extends DogException {
    private String typeName;

    public DogTypeNotExistException(String typeName) {
        super(DogErrorCode.DOG_TYPE_NOT_EXIST);
        this.typeName = typeName;
    }
}
