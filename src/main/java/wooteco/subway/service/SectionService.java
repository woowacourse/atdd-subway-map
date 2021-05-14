package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exceptions.LineNotFoundException;
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
        section.setId(sectionId);
        return section;
    }
}
