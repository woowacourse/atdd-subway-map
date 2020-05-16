package wooteco.subway.admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {

    @Query("select id from station where name = :name")
    Optional<Long> findIdByName(@Param("name") String name);

    @Override
    List<Station> findAll();

    @Query("SELECT station.id, station.name, station.created_at "
        + "FROM station INNER JOIN line_station ON station.id = line_station.station_id "
        + "WHERE line_station.line = :lineId ORDER BY line_station.line_key")
    List<Station> findAllOrderByKey(@Param("lineId") Long lineId);

    @Override
    List<Station> findAllById(Iterable<Long> longs);
}