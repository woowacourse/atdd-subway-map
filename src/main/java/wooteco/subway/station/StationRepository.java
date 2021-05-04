package wooteco.subway.station;

import java.util.List;
import java.util.Optional;

public interface StationRepository {
    Station save(Station station);

    List<Station> findAll();

    Optional<Station> findByName(String name);

    void delete(Long id);
}
