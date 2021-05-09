package wooteco.subway.dao.station;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.station.Station;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    Optional<Station> findById(Long id);

    boolean doesNotExistName(String name);

    void deleteById(Long id);
}
