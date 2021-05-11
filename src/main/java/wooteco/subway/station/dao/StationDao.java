package wooteco.subway.station.dao;

import wooteco.subway.domain.Station;

import java.util.List;
import java.util.Optional;

public interface StationDao {
    Station create(Station station);

    List<Station> findAll();

    Station findById(Long id);
    boolean existById(Long id);


    boolean existByName(String name);

    void delete(Long id);
}
