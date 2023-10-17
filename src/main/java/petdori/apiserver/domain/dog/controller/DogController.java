package petdori.apiserver.domain.dog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import petdori.apiserver.domain.dog.dto.request.DogRegisterRequestDto;
import petdori.apiserver.domain.dog.dto.response.DogDetailResponseDto;
import petdori.apiserver.domain.dog.dto.response.MyDogResponseDto;
import petdori.apiserver.domain.dog.service.DogService;
import petdori.apiserver.global.common.BaseResponse;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dog")
public class DogController {
    private final DogService dogService;

    @PostMapping("/register")
    public BaseResponse<?> register(@RequestParam(value = "dog_image", required = false) MultipartFile dogImage,
                                    @RequestParam(value = "dog_name") String dogName,
                                    @RequestParam(value = "dog_type") String dogType,
                                    @RequestParam(value = "dog_gender") String dogGender,
                                    @RequestParam(value = "is_neutered") Boolean isNeutered,
                                    @RequestParam(value = "dog_weight") BigDecimal dogWeight,
                                    @RequestParam(value = "dog_birth") String dogBirth)
    {
        DogRegisterRequestDto dogRegisterRequestDto = DogRegisterRequestDto.builder()
                .dogName(dogName).dogType(dogType).dogGender(dogGender)
                .isNeutered(isNeutered).dogWeight(dogWeight).dogBirth(dogBirth)
                .build();

        dogService.registerDog(dogImage, dogRegisterRequestDto);

        return BaseResponse.createSuccessResponseWithNoContent();
    }

    @GetMapping("/dog-types")
    public BaseResponse<List<String>> getAllDogType() {
        List<String> dogTypeNames = dogService.getAllDogTypeNames();
        return BaseResponse.createSuccessResponse(dogTypeNames);
    }

    @GetMapping("/my-dogs")
    public BaseResponse<List<MyDogResponseDto>> getMyAllDogs() {
        List<MyDogResponseDto> myDogs = dogService.getMyAllDogs();
        return BaseResponse.createSuccessResponse(myDogs);
    }

    @GetMapping("/{dogId}")
    public BaseResponse<DogDetailResponseDto> getMyDog(@PathVariable Long dogId) {
        DogDetailResponseDto dogDetail = dogService.getDogDetail(dogId);
        return BaseResponse.createSuccessResponse(dogDetail);

    }

    @DeleteMapping("/{dogId}")
    public BaseResponse<?> deleteMyDog(@PathVariable Long dogId) {
        dogService.deleteDog(dogId);
        return BaseResponse.createSuccessResponseWithNoContent();
    }

    @PatchMapping("/{dogId}")
    public BaseResponse<?> updateMyDog(@PathVariable Long dogId,
                            @RequestParam(value = "dog_image", required = false) MultipartFile dogImage,
                            @RequestParam(value = "dog_name", required = false) String dogName,
                            @RequestParam(value = "dog_type", required = false) String dogType,
                            @RequestParam(value = "dog_gender", required = false) String dogGender,
                            @RequestParam(value = "is_neutered", required = false) Boolean isNeutered,
                            @RequestParam(value = "dog_weight", required = false) BigDecimal dogWeight,
                            @RequestParam(value = "dog_birth", required = false) String dogBirth)
    {
        DogRegisterRequestDto dogUpdateRequestDto = DogRegisterRequestDto.builder()
                .dogName(dogName).dogType(dogType).dogGender(dogGender)
                .isNeutered(isNeutered).dogWeight(dogWeight).dogBirth(dogBirth)
                .build();
        dogService.updateDog(dogId, dogImage, dogUpdateRequestDto);
        return BaseResponse.createSuccessResponseWithNoContent();
    }
}
