package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.Station;

public interface StationDao {

    Station save(Station station);

    Optional<Station> findByName(String name);

    List<Station> findAll();

    void deleteById(Long id);

    void deleteAll();

    Optional<Station> findById(Long id);
}
