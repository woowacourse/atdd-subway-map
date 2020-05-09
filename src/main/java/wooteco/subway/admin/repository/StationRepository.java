package wooteco.subway.admin.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

public interface StationRepository extends CrudRepository<Station, Long> {

    @Override
    List<Station> findAllById(Iterable<Long> ids);

    @Query("SELECT * FROM STATION WHERE NAME = :name")
    Optional<Station> findByName(@Param("name") String name);

    @Query("SELECT STATION.*"
        + "  FROM  STATION"
        + " INNER JOIN LINE_STATION ON STATION.ID = LINE_STATION.STATION"
        + " WHERE LINE_STATION.LINE = :lineId"
        + " ORDER BY SEQUENCE;")
    List<Station> findAllByIdOrderBy(@Param("lineId") Long lineId);
}