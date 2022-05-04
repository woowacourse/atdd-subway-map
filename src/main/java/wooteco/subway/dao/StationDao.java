package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Station;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    int deleteById(Long id);

    Optional<Station> findById(Long id);

    boolean exists(Station station);
}
