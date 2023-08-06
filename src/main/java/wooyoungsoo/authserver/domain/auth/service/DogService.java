package wooyoungsoo.authserver.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooyoungsoo.authserver.domain.auth.dto.DogRegisterDto;
import wooyoungsoo.authserver.domain.auth.entity.dog.Dog;
import wooyoungsoo.authserver.domain.auth.entity.dog.DogType;
import wooyoungsoo.authserver.domain.auth.entity.member.Member;
import wooyoungsoo.authserver.domain.auth.exception.dog.DogTypeNotExistException;
import wooyoungsoo.authserver.domain.auth.exception.member.MemberNotExistException;
import wooyoungsoo.authserver.domain.auth.repository.DogRepository;
import wooyoungsoo.authserver.domain.auth.repository.DogTypeRepository;
import wooyoungsoo.authserver.domain.auth.repository.MemberRepository;

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
