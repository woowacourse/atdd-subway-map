package wooteco.subway.admin.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Query("SELECT id FROM station WHERE name = :stationName")
    Long findIdByName(@Param("stationName") String stationName);

    @Query("SELECT * FROM station WHERE id = :id")
    Set<Station> findAllById(@Param("id") List<Long> id);
}