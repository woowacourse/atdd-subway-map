package wooteco.subway.station.domain;

import wooteco.subway.station.domain.Station;

import java.util.List;

public interface StationRepository {

    Station save(Station station);

    Station findById(Long id);

    List<Station> findByIds(List<Long> ids);

    List<Station> findAll();

    void delete(Long id);

    void deleteAll();
}
