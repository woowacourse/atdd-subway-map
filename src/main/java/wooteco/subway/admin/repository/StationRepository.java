package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

import java.util.List;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Override
    List<Station> findAllById(Iterable<Long> longs);

    @Override
    List<Station> findAll();

    @Query("SELECT station.id, station.name, station.created_at " +
            "FROM station INNER JOIN line_station ON station.id = line_station.station " +
            "WHERE line_station.line = :lineId " +
            "ORDER BY line_station.line_key")
    List<Station> findAllByLineId(@Param("lineId") Long lineId);
}