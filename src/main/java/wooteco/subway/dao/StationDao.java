package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Station;

public interface StationDao {

    Station save(Station station);

    void deleteAll();

    List<Station> findAll();

    void delete(Station station);

    Optional<Station> findById(Long id);
}
