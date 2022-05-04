package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Station;

public interface StationDao {
    Station save(Station station);

    boolean existByName(String name);

    List<Station> findAll();

    void delete(Long id);

    boolean existById(Long id);

}
