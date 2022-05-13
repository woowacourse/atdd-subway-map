package wooteco.subway.domain.repository;

import wooteco.subway.domain.Station;

import java.util.List;
import java.util.Optional;

public interface StationRepository {

    Station save(final Station station);

    List<Station> findAll();

    void delete(Station station);

    Optional<Station> findByName(String name);

    Optional<Station> findById(Long id);

    boolean existByName(String name);
}
