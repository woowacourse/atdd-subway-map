package wooteco.subway.station;

import java.util.List;
import java.util.Optional;

public interface StationRepository {

    Station save(final Station station);

    Optional<Station> findByName(final String name);

    Optional<Station> findById(final Long id);

    List<Station> findAll();

    void delete(final Long id);
}
