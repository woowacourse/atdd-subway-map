package wooteco.subway.station.domain;

import java.util.List;

public interface StationRepository {
    Station save(Station station);

    Station findById(Long id);

    Station findByIds(List<Long> ids);

    void delete(Long id);

    List<Station> findAll();
}
