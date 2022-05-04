package wooteco.subway.dao;

import wooteco.subway.domain.Station;

import java.util.List;

public interface StationDao {

    Station save(Station station);

    List<Station> findAll();

    int deleteStation(long id);
}
