package petdori.apiserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petdori.apiserver.domain.auth.dto.DogRegisterDto;
import petdori.apiserver.domain.auth.entity.dog.Dog;
import petdori.apiserver.domain.auth.entity.dog.DogType;
import petdori.apiserver.domain.auth.entity.member.Member;
import petdori.apiserver.domain.auth.exception.dog.DogTypeNotExistException;
import petdori.apiserver.domain.auth.exception.member.MemberNotExistException;
import petdori.apiserver.domain.auth.repository.DogRepository;
import petdori.apiserver.domain.auth.repository.DogTypeRepository;
import petdori.apiserver.domain.auth.repository.MemberRepository;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class DogService {
    private final MemberRepository memberRepository;
    private final DogTypeRepository dogTypeRepository;
    private final DogRepository dogRepository;

    public void registerDog(DogRegisterDto dogRegisterDto) {
        Member owner = memberRepository.findByEmail(dogRegisterDto.getOwnerEmail())
                .orElseThrow(() -> new MemberNotExistException(dogRegisterDto.getOwnerEmail()));

        DogType dogType = dogTypeRepository.findByTypeName(dogRegisterDto.getDogType())
                .orElseThrow(() -> new DogTypeNotExistException(dogRegisterDto.getDogType()));

        Dog dog = Dog.from(owner, dogType, dogRegisterDto);
        dogRepository.save(dog);
    }
}
