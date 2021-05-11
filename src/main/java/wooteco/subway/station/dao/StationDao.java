package wooteco.subway.station.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;

@Repository
public interface StationDao {
    Station save(Station station);

    Optional<Station> findById(Long stationId);

    Optional<Station> findByName(String stationName);

    List<Station> findAllByIds(Set<Long> stationIds);

    List<Station> findAll();

    void delete(Long id);
}
