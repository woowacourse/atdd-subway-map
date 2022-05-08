package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Station;

public interface StationDao {

    Station save(Station station);

    boolean existsByName(String name);

    Optional<Station> findById(Long id);

    List<Station> findAll();

    void deleteById(Long id);
}
