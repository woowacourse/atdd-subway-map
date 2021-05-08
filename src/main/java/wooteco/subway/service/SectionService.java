package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public long createSection(long upStationId, long downStationId, int distance, long lineId) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);
        Section section = new Section(upStation, downStation, distance, lineId);
        return sectionDao.save(section);
    }

    public List<Station> findStationsOfLine(long lineId) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        return null;
    }
}
