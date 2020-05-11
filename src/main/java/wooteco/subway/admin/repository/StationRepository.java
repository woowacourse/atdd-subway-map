package wooteco.subway.admin.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Override
    List<Station> findAllById(Iterable<Long> ids);

    @Query("SELECT count(*) FROM station WHERE name = :name")
    boolean existsStationBy(@Param("name") String name);
}