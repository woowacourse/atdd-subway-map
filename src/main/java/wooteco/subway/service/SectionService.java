package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void createSection(Long lineId, Section section) {
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        sections.addSection(section);

        sectionDao.saveSections(lineId, sections);
    }

    public Sections getSectionsByLineId(Long lineId) {
        return sectionDao.findSectionsByLineId(lineId);
    }

    public void deleteStationById(Long lineId, Long stationId) {
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        sections.deleteStation(stationId);

        sectionDao.saveSections(lineId, sections);
    }
}
