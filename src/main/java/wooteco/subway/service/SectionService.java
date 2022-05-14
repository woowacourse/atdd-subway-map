package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void createSection(Long lineId, SectionRequest sectionRequest) {
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        sections.addSection(sectionRequest.toSection());

        saveSections(lineId, sections);
    }

    public Sections getSectionsByLineId(Long lineId) {
        return sectionDao.findSectionsByLineId(lineId);
    }

    public void deleteStationById(Long lineId, Long stationId) {
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        sections.deleteStation(stationId);

        saveSections(lineId, sections);
    }

    private void saveSections(Long lineId, Sections sections) {
        sectionDao.removeAllSectionsByLineId(lineId);

        for (Section section : sections.getValue()) {
            sectionDao.save(lineId, section);
        }
    }
}
