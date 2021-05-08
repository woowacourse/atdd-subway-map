package wooteco.subway.station.dao;

import wooteco.subway.station.Station;

import java.util.List;
import java.util.Optional;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    Optional<Station> findStationByName(String name);

    void remove(Long id);

    void removeAll();
}
