package wooteco.subway.dao;

import wooteco.subway.domain.Station;

import java.util.List;

public interface StationDao {

    Station save(Station station);

    Station findByName(String name);

    List<Station> findAll();

    void deleteById(Long id);
}
