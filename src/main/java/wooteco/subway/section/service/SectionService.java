package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.api.dto.SectionRequest;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.model.Section;
import wooteco.subway.section.model.Sections;
import wooteco.subway.station.dao.StationDao;

import java.util.List;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void save(Long lineId, SectionRequest sectionRequest) {
        List<Section> currentSections = sectionDao.findSectionsByLineId(lineId);
        Sections sections = new Sections(currentSections);
        sections.add(convertToSection(lineId, sectionRequest));
        updateSections(lineId, sections);
    }

    private Section convertToSection(Long lineId, SectionRequest sectionRequest) {
        return Section.builder()
                .line(lineDao.findLineById(lineId))
                .upStation(stationDao.findStationById(sectionRequest.getUpStationId()))
                .downStation(stationDao.findStationById(sectionRequest.getDownStationId()))
                .distance(sectionRequest.getDistance())
                .build();
    }

    @Transactional
    public void deleteById(Long lineId, Long stationId) {
        List<Section> currentSections = sectionDao.findSectionsByLineId(lineId);
        Sections sections = new Sections(currentSections);
        sections.delete(stationId);
        updateSections(lineId, sections);
    }

    private void updateSections(Long lineId, Sections sections) {
        sectionDao.deleteAllByLineId(lineId);
        sectionDao.saveAll(sections.sections());
    }
}
