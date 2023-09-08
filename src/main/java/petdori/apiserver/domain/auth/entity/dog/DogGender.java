package petdori.apiserver.domain.auth.entity.dog;

import petdori.apiserver.domain.auth.exception.dog.InvalidDogGenderException;

public enum DogGender {
    MALE, FEMALE;

    public static DogGender getDogGenderByGenderName(String genderName) {
        return switch(genderName) {
            case "수컷" -> DogGender.MALE;
            case "암컷" -> DogGender.FEMALE;
            default -> throw new InvalidDogGenderException();
        };
    }
}
