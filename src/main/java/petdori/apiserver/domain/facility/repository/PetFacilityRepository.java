package petdori.apiserver.domain.facility.repository;

import org.geolatte.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import petdori.apiserver.domain.facility.entity.PetFacility;

import java.util.List;

public interface PetFacilityRepository extends JpaRepository<PetFacility, Long> {
    @Query(value = "SELECT id, name, address, location, ST_Distance_Sphere(location, ST_GeomFromText(CONCAT('POINT(', ?1, ' ', ?2, ')'), 4326)) as 'distance'\n" +
            "FROM pet_facility\n" +
            "WHERE ST_Contains(ST_Buffer(ST_GeomFromText(CONCAT('POINT(', ?1, ' ', ?2, ')'), 4326), ?3), location) AND pet_facility_type_id IN ?4 \n" +
            "ORDER BY distance\n" +
            "LIMIT 15",
            nativeQuery = true)
    List<NearByFacilityInfo> findByDistance(double latitude, double longitude, double radius, List<Long> filteredTypeIds);

    interface NearByFacilityInfo {
        Long getId();
        String getName();
        String getAddress();
        Point getLocation();
        double getDistance();
    }
}
