package wooteco.subway.dao.station;

import wooteco.subway.domain.Station;

import java.util.List;
import java.util.Optional;

public interface StationDao {
    Station save(Station station);

    List<Station> findAll();

    Optional<Station> findStationById(Long id);

    Optional<Station> findStationByName(String name);

    void delete(Long id);
}
