package wooteco.subway.station.dao;

import wooteco.subway.station.Station;

import java.util.List;
import java.util.Optional;

public interface StationDao {
    Station save(Station station);

    Optional<Station> findByName(String name);

    List<Station> findAll();

    void delete(Long id);

    Optional<Station> findById(Long id);
}
