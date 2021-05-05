package wooteco.subway.station.repository;

import wooteco.subway.station.Station;

import java.util.List;

public interface StationRepository {
    Station save(Station station);

    List<Station> findAll();

    void deleteById(Long id);
}
