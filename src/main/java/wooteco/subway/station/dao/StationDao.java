package wooteco.subway.station.dao;

import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationName;

import java.util.List;

public interface StationDao {
    Station save(Station station);

    List<Station> findAll();

    Station findById(Long id);

    boolean checkExistName(StationName name);

    void delete(Station station);

    void deleteAll();
}
