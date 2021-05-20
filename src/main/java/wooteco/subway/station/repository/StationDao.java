package wooteco.subway.station.repository;

import wooteco.subway.station.Station;

import java.util.List;

public interface StationDao {
    Station save(Station station);

    List<Station> findAll();

    boolean findByName(String name);

    void deleteById(Long id);

    Station findBy(Long id);

    boolean isExistingStation(Long stationId);
}
