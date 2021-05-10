package wooteco.subway.section.service;

import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.OrderedSections;
import wooteco.subway.section.domain.SectionDistance;
import wooteco.subway.station.StationService;
import wooteco.subway.station.domain.Station;

public class SectionService {
    private final StationService stationService;
    private final SectionDao sectionDao;

    public SectionService(StationService stationService, SectionDao sectionDao) {
        this.stationService = stationService;
        this.sectionDao = sectionDao;
    }

    public void addSection(long lineId, Station station, SectionDistance distance) {
        findSections(lineId);
    }

    public OrderedSections findSections(long lineId) {
        return sectionDao.findByLineId(lineId);
    }

    public void updateSections(long lineId, OrderedSections orderedSections) {

    }
}
