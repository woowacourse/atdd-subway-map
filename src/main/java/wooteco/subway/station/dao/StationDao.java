package wooteco.subway.station.dao;

import wooteco.subway.domain.Station;

import java.util.List;

public interface StationDao {
    Station create(Station station);

    List<Station> findAll();

    Station findById(Long id);

    boolean existById(Long id);

    boolean existByName(String name);

    void removeById(Long id);
}
