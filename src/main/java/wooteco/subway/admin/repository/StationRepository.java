package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

import java.util.List;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Query("SELECT * FROM STATION WHERE NAME IN (:names);")
    List<Station> findStationsByNames(@Param("names") List<String> names);

    @Query("SELECT COUNT(*) FROM STATION WHERE NAME = :name LIMIT 1")
    int countSameStationByName(@Param("name") String name);
}
