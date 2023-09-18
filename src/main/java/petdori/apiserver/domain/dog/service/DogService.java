package petdori.apiserver.domain.dog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import petdori.apiserver.domain.dog.dto.request.DogRegisterRequestDto;
import petdori.apiserver.domain.dog.entity.Dog;
import petdori.apiserver.domain.dog.entity.DogType;
import petdori.apiserver.domain.auth.entity.member.Member;
import petdori.apiserver.domain.dog.exception.DogTypeNotExistException;
import petdori.apiserver.domain.auth.exception.member.MemberNotExistException;
import petdori.apiserver.domain.dog.repository.DogRepository;
import petdori.apiserver.domain.dog.repository.DogTypeRepository;
import petdori.apiserver.domain.auth.repository.MemberRepository;
import petdori.apiserver.global.common.S3Uploader;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class DogService {
    private final MemberRepository memberRepository;
    private final DogTypeRepository dogTypeRepository;
    private final DogRepository dogRepository;
    private final S3Uploader s3Uploader;

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
}
