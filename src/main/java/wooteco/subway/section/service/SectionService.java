package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

@Service
public class SectionService {
    private final StationService stationService;
    private final SectionDao sectionDao;

    public SectionService(StationService stationService, SectionDao sectionDao) {
        this.stationService = stationService;
        this.sectionDao = sectionDao;
    }

    public void initSection(Long lineId, SectionRequest sr) {
        stationService.findById(sr.getUpStationId());
        stationService.findById(sr.getDownStationId());

        sectionDao.save(lineId, sr);
    }

    public void addSection(Long lineId, SectionRequest sr) {
        Sections sections = sectionsByLineId(lineId);
        sections.add(sectionOf(sr));

        updateSections(lineId, sections);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = sectionsByLineId(lineId);
        sections.delete(stationService.findById(stationId));

        updateSections(lineId, sections);
    }

    private Section sectionOf(SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());

        return new Section(upStation, downStation, sectionRequest.getDistance());
    }

    private void updateSections(Long lineId, Sections sections) {
        sectionDao.deleteSectionsOf(lineId);
        sectionDao.saveSections(lineId, sections.getSections());
    }

    public Sections sectionsByLineId(Long id) {
        return new Sections(sectionDao.findSections(id));
    }
}
