package wooteco.subway.station;

import java.util.List;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.section.Sections;
import wooteco.subway.station.dto.StationResponse;

@Repository
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<StationResponse> findByLineId(Long lineId, Sections sections) {
        Stations stations = new Stations(stationDao.findByLineId(lineId));
        stations.sort(sections);
        return stations.toResponse();
    }
}
