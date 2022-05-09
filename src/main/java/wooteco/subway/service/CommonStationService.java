package wooteco.subway.service;

import java.util.List;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

public interface CommonStationService {

    Station save(final StationRequest stationRequest);

    List<Station> findAll();

    void deleteById(final Long id);
}
