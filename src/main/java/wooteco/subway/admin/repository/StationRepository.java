package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Query("SELECT id FROM station WHERE name = :name ")
    Long findIdByName(@Param("name") String stationName);
}