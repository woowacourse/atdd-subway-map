package wooteco.subway.admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import wooteco.subway.admin.domain.LineStation;

public interface LineStationRepository extends CrudRepository<LineStation, Long> {
    @Query("SELECT * FROM LINE_STATION WHERE line = :id")
    List<LineStation> findAllByLine(@Param("id") Long id);
}
