package petdori.apiserver.domain.facility.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import petdori.apiserver.domain.facility.dto.request.NearbyFacilityRequestDto;
import petdori.apiserver.domain.facility.dto.response.NearbyFacilityResponseDto;
import petdori.apiserver.domain.facility.service.PetFacilityService;
import petdori.apiserver.global.common.BaseResponse;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/facility")
public class PetFacilityController {
    private final PetFacilityService petFacilityService;

    @GetMapping("/nearby-facilities")
    private BaseResponse<List<NearbyFacilityResponseDto>> getNearbyFacilities(
            @RequestBody NearbyFacilityRequestDto nearbyFacilityRequestDto,
            @RequestParam(value = "keyword", required = false) String[] keywords
            ) {
        List<NearbyFacilityResponseDto> nearByFacilities = petFacilityService.getNearByFacilities(nearbyFacilityRequestDto, keywords);
        return BaseResponse.createSuccessResponse(nearByFacilities);
    }
}
