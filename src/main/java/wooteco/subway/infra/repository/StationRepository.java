package wooteco.subway.infra.repository;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Station;

public interface StationRepository {

    List<Station> findAll();

    Optional<Station> findById(Long id);

    Station save(Station station);

    boolean existByName(String name);

    boolean existById(Long id);

    long deleteById(Long id);
}
