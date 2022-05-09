package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.station.Station;

public interface StationDao {

    Long save(Station station);

    List<Station> findAll();

    Optional<Station> findById(Long id);

    Boolean existsByName(String name);

    void remove(Long id);
}
