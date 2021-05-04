package wooteco.subway.station.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.station.Station;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    Optional<Station> findStationByName(String name);

    void remove(Long id);
}
