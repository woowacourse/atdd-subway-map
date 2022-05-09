package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import java.util.List;

@Service
public class SectionService {
    private final StationDao stationDao;

    public SectionService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<Station> getBothOfStations(Section section) {
        return List.of(stationDao.findById(section.getUpStationId()), stationDao.findById(section.getDownStationId()));
    }
}
