package wooteco.subway.admin.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import wooteco.subway.admin.domain.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface StationRepository extends CrudRepository<Station, Long> {
    @Override
    default List<Station> findAllById(Iterable<Long> lineStationIds) {
        List<Station> stations = new ArrayList<>();

        for (Long lineStationId : lineStationIds) {
            Station station = findById(lineStationId)
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 로우가 없습니다."));
            stations.add(station);
        }

        return stations;
    }

    @Query("select * from station where name = :name")
    Optional<Station> findByName(@Param("name") String name);
}