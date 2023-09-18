package petdori.apiserver.domain.dog.entity;

import petdori.apiserver.domain.dog.exception.InvalidDogGenderException;

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
