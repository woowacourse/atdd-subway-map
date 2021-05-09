package wooteco.subway.station.dao;

import wooteco.subway.station.Station;

import java.util.List;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    boolean isExistByName(String name);

    void remove(Long id);

    void removeAll();
}
