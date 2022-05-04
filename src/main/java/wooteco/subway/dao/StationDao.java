package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Station;

public interface StationDao {
    Station save(Station station);

    List<Station> findAll();

    Optional<Station> findByName(String name);

    Integer deleteById(Long id);
}
