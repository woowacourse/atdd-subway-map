package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Long lineId, Long upStationId, Long downStationId, int distance) {
        sectionDao.save(lineId, upStationId, downStationId, distance);
    }

    public List<StationResponse> findSectionById(Long id) {  // TODO : 구간 순서 정렬하기
        List<StationResponse> result = new ArrayList<>();
        Map<Station, Station> sectionMap = sectionDao.findSectionById(id);
        for (Station upStation : sectionMap.keySet()) {
            result.add(new StationResponse(upStation.getId(), upStation.getName()));

            Station downStation = sectionMap.get(upStation);
            result.add(new StationResponse(downStation.getId(), downStation.getName()));
        }
        return result;
    }
}
