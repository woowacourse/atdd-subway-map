package wooteco.subway.station.repository;

import wooteco.subway.station.domain.Station;

import java.util.List;

public interface StationRepository {

    Station save(Station station);

    List<Station> findAll();

    Station findById(Long id);

    void delete(Long id);
}
