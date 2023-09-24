package petdori.apiserver.domain.facility.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petdori.apiserver.domain.facility.dto.request.NearbyFacilityRequestDto;
import petdori.apiserver.domain.facility.dto.response.NearbyFacilityResponseDto;
import petdori.apiserver.domain.facility.entity.PetFacilityOperatingHour;
import petdori.apiserver.domain.facility.entity.PetFacilityType;
import petdori.apiserver.domain.facility.exception.FacilityTypeNotExistException;
import petdori.apiserver.domain.facility.repository.PetFacilityOperatingHourRepository;
import petdori.apiserver.domain.facility.repository.PetFacilityRepository;
import petdori.apiserver.domain.facility.repository.PetFacilityRepository.NearByFacilityInfo;
import petdori.apiserver.domain.facility.repository.PetFacilityTypeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class PetFacilityService {
    private final PetFacilityRepository petFacilityRepository;
    private final PetFacilityOperatingHourRepository petFacilityOperatingHourRepository;
    private final PetFacilityTypeRepository petFacilityTypeRepository;

    public List<NearbyFacilityResponseDto> getNearByFacilities(
            NearbyFacilityRequestDto nearbyFacilityRequestDto,
            String[] keywords
    ) {
        // keyword라는 이름의 쿼리스트링으로 전달받은 값이 하나도 없을 경우 빈 리스트를 반환
        if (keywords == null) {
            return new ArrayList<>();
        }

        List<Long> filteredTypeIds = new ArrayList<>();
        for (String typeName : keywords) {
            Long typeId = petFacilityTypeRepository.findIdByTypeName(typeName)
                    .orElseThrow(
                            () -> new FacilityTypeNotExistException(typeName)
                    ).getId();
            filteredTypeIds.add(typeId);
        }

        double currentLatitude = nearbyFacilityRequestDto.getLatitude();
        double currentLongitude = nearbyFacilityRequestDto.getLongitude();
        double radius = nearbyFacilityRequestDto.getRadius();

        List<NearByFacilityInfo> nearByFacilityInfos = petFacilityRepository
                .findByDistance(currentLongitude, currentLatitude, radius, filteredTypeIds);
        List<NearbyFacilityResponseDto> nearbyFacilities = new ArrayList<>();

        for (NearByFacilityInfo nearByFacilityInfo : nearByFacilityInfos) {
            Long petFacilityId = nearByFacilityInfo.getId();
            double latitude = nearByFacilityInfo.getLocation().getPosition().getCoordinate(1);
            double longitude = nearByFacilityInfo.getLocation().getPosition().getCoordinate(0);
            NearbyFacilityResponseDto nearbyFacilityResponseDto = NearbyFacilityResponseDto.builder()
                    .name(nearByFacilityInfo.getName())
                    .address(nearByFacilityInfo.getAddress())
                    .latitude(latitude)
                    .longitude(longitude)
                    .distanceInfo(convertDistance(nearByFacilityInfo.getDistance()))
                    .operatingHourInfo(getOperatingHourInfo(petFacilityId))
                    .build();
            nearbyFacilities.add(nearbyFacilityResponseDto);
        }

        return nearbyFacilities;
    }

    private String convertDistance(double distance) {
        if (distance < 1000) {
            return String.format("%.0f", distance) + "m";
        }
        return String.format("%.2f", distance / 1000) + "km";
    }

    private String getOperatingHourInfo(Long petFacilityId) {
        List<PetFacilityOperatingHour> operatingHours = petFacilityOperatingHourRepository.findByPetFacilityId(petFacilityId);
        StringBuffer operatingInfoBuffer = new StringBuffer();
            if (!operatingHours.isEmpty()) {
                String openHour = operatingHours.get(0).getOpenHour().substring(0, 5);
                String closeHour = operatingHours.get(0).getCloseHour().substring(0, 5);
                if (operatingHours.size() == 7) {
                    operatingInfoBuffer.append("매일 ")
                            .append(openHour)
                            .append(" ~ ")
                            .append(closeHour);
                } else{
                    for (int i=0; i < operatingHours.size(); i++) {
                        PetFacilityOperatingHour operatingHour = operatingHours.get(i);
                        operatingInfoBuffer.append(operatingHour.getDayOfTheWeek().getValue());
                        if (i != operatingHours.size() - 1) {
                            operatingInfoBuffer.append(",");
                        }
                    }
                    operatingInfoBuffer.append(" ")
                            .append(openHour)
                            .append(" ~ ")
                            .append(closeHour);
                }
            }
        return operatingInfoBuffer.toString();
    }
}
