package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Station;

public interface StationDao {

    Long save(Station station);

    Optional<Station> findById(Long id);

    List<Station> findAll();

    int deleteById(Long id);
}
