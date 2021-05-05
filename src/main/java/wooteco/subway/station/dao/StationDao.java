package wooteco.subway.station.dao;

import wooteco.subway.station.domain.Station;

import java.util.List;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    Station findById(Long id);

    void delete(Long id);
}
