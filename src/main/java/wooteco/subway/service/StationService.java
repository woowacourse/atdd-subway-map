package wooteco.subway.service;

import java.util.List;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.StationServiceRequest;

public interface StationService {

    Station save(StationServiceRequest stationServiceRequest);

    List<Station> findAll();

    void deleteById(Long id);

    Station findById(Long stationId);
}
