package wooteco.subway.dao;

import wooteco.subway.domain.Station;

import java.util.List;

public interface StationDao {

    Station create(Station station);

    Station findById(Long upStationId);

    Station findByName(String name);

    List<Station> findAll();

    void deleteById(Long id);

    boolean existByName(String name);

    boolean existById(Long id);
}
