package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Station;

public interface StationDao {

    Station save(Station station);

    boolean isExistById(Long id);

    boolean isExistByName(String name);

    Station findById(long id);

    List<Station> findAll();

    int delete(long id);
}
