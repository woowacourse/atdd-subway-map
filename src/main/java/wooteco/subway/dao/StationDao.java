package wooteco.subway.dao;

import wooteco.subway.domain.Station;

import java.util.List;
import java.util.Optional;

public interface StationDao {

    Long save(Station station);

    Optional<Station> findById(Long id);

    List<Station> findAll();

    boolean hasStation(String name);

    void deleteById(Long id);
}
