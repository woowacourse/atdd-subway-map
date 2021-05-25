package wooteco.subway.station.infra;

import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.Optional;

public interface StationDao {

    Station save(final Station station);

    Optional<Station> findById(final Long id);

    List<Station> findByIds(List<Long> ids);

    List<Station> findAll();

    void delete(final Long id);

    void deleteAll();
}
