package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Station;

public interface StationDao {

    Optional<Station> insert(Station station);

    List<Station> findAll();

    Integer deleteById(Long id);
}
