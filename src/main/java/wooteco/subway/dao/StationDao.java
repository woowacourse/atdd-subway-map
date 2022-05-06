package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

public interface StationDao {

    Station save(StationRequest stationRequest);

    List<Station> findAll();

    int deleteStation(long id);
}
