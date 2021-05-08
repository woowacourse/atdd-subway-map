package wooteco.subway.dao.station;

import wooteco.subway.domain.Station;

import java.util.List;
import java.util.Optional;

public interface StationDao {
    Station save(Station station);

    List<Station> findAll();

    Optional<Station> findById(Long id);

    Optional<Station> findByName(String name);

    void clear();

    void delete(Long id);
}
