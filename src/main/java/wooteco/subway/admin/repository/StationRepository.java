package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

import java.util.List;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Override
    List<Station> findAll();

    @Query("SELECT * FROM station WHERE id IN (:stationIds)")
    List<Station> findAllById(@Param("stationIds") List<Long> stationIds);
}