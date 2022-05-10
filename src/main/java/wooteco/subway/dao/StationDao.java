package wooteco.subway.dao;

import wooteco.subway.domain.Station;

import java.util.List;
import java.util.Optional;

public interface StationDao {
    long save(Station station);

    List<Station> findAll();

    Optional<Station> findById(Long id);

    boolean existById(Long id);

    boolean existByName(String name);

    void deleteById(Long id);

    void deleteAll();
}
