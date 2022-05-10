package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Station;

public interface StationDao {
    Station save(Station station);

    Station getStation(Long id);

    boolean existByName(String name);

    List<Station> findAll();

    void delete(Long id);

    boolean existById(Long id);
}
