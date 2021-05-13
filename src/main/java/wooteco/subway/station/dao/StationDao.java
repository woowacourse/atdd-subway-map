package wooteco.subway.station.dao;

import wooteco.subway.station.Station;

import java.util.List;

public interface StationDao {
    Station save(Station station);

    Station findByName(String name);

    List<Station> findAll();

    void delete(Long id);

    Station findById(Long id);

    boolean existByName(String name);

    boolean existById(Long id);
}
