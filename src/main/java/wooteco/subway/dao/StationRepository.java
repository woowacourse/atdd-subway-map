package wooteco.subway.dao;

import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

import java.util.List;

public interface StationRepository {

    Station save(final Station station);

    List<Station> findAll();

    void deleteById(Long id);

    Station findByName(String name);

    Station findById(Long id);
}
