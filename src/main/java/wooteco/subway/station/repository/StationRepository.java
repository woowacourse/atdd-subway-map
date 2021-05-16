package wooteco.subway.station.repository;

import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.Optional;

public interface StationRepository {

    Station save(Station station);

    Optional<Station> findById(Long id);

    Optional<Station> findByName(String name);

    List<Station> findByIds(List<Long> ids);

    List<Station> findAll();

    void delete(Long id);

    void deleteAll();
}
