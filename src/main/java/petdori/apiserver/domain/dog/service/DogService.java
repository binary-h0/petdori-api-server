package petdori.apiserver.domain.dog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import petdori.apiserver.domain.dog.dto.request.DogRegisterRequestDto;
import petdori.apiserver.domain.dog.dto.response.DogDetailResponseDto;
import petdori.apiserver.domain.dog.dto.response.MyDogResponseDto;
import petdori.apiserver.domain.dog.entity.Dog;
import petdori.apiserver.domain.dog.entity.DogType;
import petdori.apiserver.domain.auth.entity.member.Member;
import petdori.apiserver.domain.dog.exception.DogNotExistException;
import petdori.apiserver.domain.dog.exception.DogOwnerNotMatchedException;
import petdori.apiserver.domain.dog.exception.DogTypeNotExistException;
import petdori.apiserver.domain.auth.exception.member.MemberNotExistException;
import petdori.apiserver.domain.dog.repository.DogRepository;
import petdori.apiserver.domain.dog.repository.DogTypeRepository;
import petdori.apiserver.domain.auth.repository.MemberRepository;
import petdori.apiserver.global.common.S3Uploader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class DogService {
    private final MemberRepository memberRepository;
    private final DogTypeRepository dogTypeRepository;
    private final DogRepository dogRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public void registerDog(MultipartFile dogImage, DogRegisterRequestDto dogRegisterRequestDto) {
        // 인증된 사용자이므로 이메일을 가져올 수 있다
        String ownerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member owner = memberRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new MemberNotExistException(ownerEmail));

        DogType dogType = dogTypeRepository.findByTypeName(dogRegisterRequestDto.getDogType())
                .orElseThrow(() -> new DogTypeNotExistException(dogRegisterRequestDto.getDogType()));
        // 클라이언트가 반려견 이미지를 첨부하지 않았을 경우에 대한 처리
        String dogImageUrl = dogImage.isEmpty() ? null : s3Uploader.uploadDogImage(dogImage);

        Dog dog = Dog.from(owner, dogType, dogImageUrl, dogRegisterRequestDto);
        dogRepository.save(dog);
    }

    @Transactional(readOnly = true)
    public List<String> getAllDogTypeNames() {
        List<String> dogTypeNames = new ArrayList<>();
        List<DogType> dogTypes = dogTypeRepository.findAll();

        for (DogType dogType : dogTypes) {
            dogTypeNames.add(dogType.getTypeName());
        }

        return dogTypeNames;
    }

    @Transactional(readOnly = true)
    public List<MyDogResponseDto> getMyAllDogs() {
        List<MyDogResponseDto> myDogs = new ArrayList<>();

        // 인증된 사용자이므로 이메일을 가져올 수 있다
        String ownerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member owner = memberRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new MemberNotExistException(ownerEmail));

        for (Dog dog : owner.getDogs()) {
            Long dogId = dog.getId();
            String dogImageUrl = dog.getDogImageUrl();
            String dogName = dog.getDogName();
            String dogTypeName = dog.getDogType().getTypeName();

            myDogs.add(MyDogResponseDto.builder().dogId(dogId).dogImageUrl(dogImageUrl)
                    .dogName(dogName).dogTypeName(dogTypeName).build());
        }

        return myDogs;
    }

    @Transactional(readOnly = true)
    public DogDetailResponseDto getDogDetail(Long dogId) {
        Dog dog = getMyDog(dogId);
        return DogDetailResponseDto.builder()
                .dogId(dog.getId())
                .dogImageUrl(dog.getDogImageUrl())
                .dogName(dog.getDogName())
                .dogTypeName(dog.getDogType().getTypeName())
                .dogGender(dog.getDogGender().name())
                .isNeutered(dog.isNeutered())
                .dogWeight(dog.getDogWeight())
                .dogBirth(dog.getDogBirth())
                .build();
    }

    @Transactional
    public void deleteDog(Long dogId) {
        Dog dog = getMyDog(dogId);
        dogRepository.delete(dog);
    }

    private Dog getMyDog(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(DogNotExistException::new);
        validateDogOwner(dog);
        return dog;
    }

    private void validateDogOwner(Dog dog) {
        // 인증된 사용자이므로 이메일을 가져올 수 있다
        String ownerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member ownerFromEmail = memberRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new MemberNotExistException(ownerEmail));

        Member ownerFromDog = dog.getOwner();

        if (!ownerFromEmail.equals(ownerFromDog)) {
            throw new DogOwnerNotMatchedException();
        }
    }
}
