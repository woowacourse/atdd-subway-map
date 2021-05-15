package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.exception.NotRemovableSectionException;
import wooteco.subway.repository.LineDao;
import wooteco.subway.repository.SectionDao;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(LineDao lineDao, SectionDao sectionDao,
        StationService stationService) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public Section createSection(SectionRequest sectionRequest, Long lineId) {
        Section section = new Section(
            stationService.findById(sectionRequest.getUpStationId()),
            stationService.findById(sectionRequest.getDownStationId()),
            sectionRequest.getDistance()
        );

        Line line = lineDao.findById(lineId)
            .orElseThrow(LineNotFoundException::new);
        Sections sections = sectionDao.findByLine(lineId);
        line.setSections(sections);
        line.addSection(section);
        Long sectionId = sectionDao.save(section, lineId);

        List<Section> affectedSections = line.findAffectedSectionByAddSection(section);
        for (Section affectedSection : affectedSections) {
            sectionDao.update(affectedSection, lineId);
        }

        section.setId(sectionId);
        return section;
    }

    public void deleteSection(Long lindId, Long stationId) {
        Line line = lineDao.findById(lindId)
            .orElseThrow(LineNotFoundException::new);
        Station station = stationService.findById(stationId);

        if (sectionDao.findByStation(lindId, stationId).size() < 2) {
            throw new NotRemovableSectionException();
        }

        Section affectedSection = line.findAffectedSectionByDeleteStation(station);
        line.removeSectionsBy(station);
        sectionDao.deleteByStation(lindId, stationId);
        sectionDao.save(affectedSection, lindId);
    }
}
