package wooteco.subway.dao.station;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Station;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    Optional<Station> findStationByName(String name);

    void remove(Long id);

    void removeAll();

    Optional<Station> findStationById(Long id);
}
