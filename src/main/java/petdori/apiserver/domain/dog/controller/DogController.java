package petdori.apiserver.domain.dog.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import petdori.apiserver.domain.dog.dto.request.DogRegisterRequestDto;
import petdori.apiserver.domain.dog.service.DogService;
import petdori.apiserver.global.common.BaseResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dog")
public class DogController {
    private final DogService dogService;

    @PostMapping("/register")
    public BaseResponse<?> register(@RequestParam(value = "dog_image") MultipartFile dogImage,
                                    @RequestParam(value = "dog_name") String dogName,
                                    @RequestParam(value = "dog_type") String dogType,
                                    @RequestParam(value = "dog_gender") String dogGender,
                                    @RequestParam(value = "is_neutered") Boolean isNeutered,
                                    @RequestParam(value = "dog_birth") String dogBirth) {
        DogRegisterRequestDto dogRegisterRequestDto = DogRegisterRequestDto.builder()
                .dogName(dogName).dogType(dogType).dogGender(dogGender)
                .isNeutered(isNeutered).dogBirth(dogBirth).build();

        dogService.registerDog(dogImage, dogRegisterRequestDto);

        return BaseResponse.createSuccessResponseWithNoContent();
    }
}
