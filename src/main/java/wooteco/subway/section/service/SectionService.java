package wooteco.subway.section.service;

import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.SectionDistance;
import wooteco.subway.station.StationService;
import wooteco.subway.station.domain.Station;

import java.util.List;

public class SectionService {
    private final StationService stationService;
    private final SectionDao sectionDao;

    public SectionService(StationService stationService, SectionDao sectionDao) {
        this.stationService = stationService;
        this.sectionDao = sectionDao;
    }

    public void addSection(long lineId, Station station, SectionDistance distance) {
        List<Section> sections = sectionDao.findByLineId(lineId);
    }
}
