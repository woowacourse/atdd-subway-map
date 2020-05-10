package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Override
    List<Station> findAll();

    Optional<Station> findById(Long id);

    @Query("SELECT * FROM station WHERE id IN (:stationIds)")
    List<Station> findAllById(@Param("stationIds") List<Long> stationIds);
}