package wooteco.subway.dao;

import wooteco.subway.domain.Station;

import java.util.List;

public interface StationDao {

    Long save(Station station);

    Station findById(Long id);

    List<Station> findAll();

    boolean hasStation(String name);

    void deleteById(Long id);
}
