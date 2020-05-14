package wooteco.subway.admin.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Line;

public interface LineRepository extends CrudRepository<Line, Long> {

    @Override
    List<Line> findAll();

    @Query("SELECT * FROM LINE WHERE NAME = :name")
    Optional<Line> findByName(@Param("name") String name);

    @Query("SELECT *"
        + "  FROM LINE"
        + " WHERE LINE_ID IN (SELECT DISTINCT(ID)"
        + "                     FROM LINE"
        + "                     JOIN LINE_STATION ON LINE.ID = LINE_STATION.LINE"
        + "                    WHERE LINE_STATION.STATION = :stationId)")
    List<Line> findLinesByStationId(@Param("stationId") Long stationId);
}
