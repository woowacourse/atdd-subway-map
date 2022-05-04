package wooteco.subway.service;

import java.util.List;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

public interface StationService {

    Station save(StationRequest stationRequest);

    List<Station> findAll();

    void deleteById(Long id);
}
