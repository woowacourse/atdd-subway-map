package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResponse;
import wooteco.subway.exception.notFoundException.LineNotFoundException;
import wooteco.subway.exception.notAddableSectionException.SameStationSectionException;
import wooteco.subway.exception.notFoundException.StationNotFoundException;
import wooteco.subway.repository.LineDao;
import wooteco.subway.repository.SectionDao;
import wooteco.subway.repository.StationDao;

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

    public SectionResponse createSectionByDto(Long lineId, SectionRequest sectionRequest) {
        return createSection(
            lineId,
            sectionRequest.getUpStationId(),
            sectionRequest.getDownStationId(),
            sectionRequest.getDistance()
        );
    }

    public SectionResponse createSection(
        Long lineId,
        Long upStationId,
        Long downStationId,
        int distance
    ) {
        Station upStation = findStation(upStationId);
        Station downStation = findStation(downStationId);

        Section section = new Section(upStation, downStation, distance);
        Sections sections = sectionDao.findByLineId(lineId);

        sections.addSection(section);

        Line line = lineDao.findById(lineId)
            .orElseThrow(LineNotFoundException::new);
        line.setSections(sections);

        sectionDao.deleteByLineId(lineId);
        sectionDao.saveSections(lineId, sections.getSections());
        return SectionResponse.of(section);
    }

    private Station findStation(Long stationId) {
        return stationDao.findById(stationId)
            .orElseThrow(StationNotFoundException::new);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = sectionDao.findByLineId(lineId);
        Station station = stationDao.findById(stationId)
            .orElseThrow(StationNotFoundException::new);
        sections.removeSection(station);

        sectionDao.deleteByLineId(lineId);
        sectionDao.saveSections(lineId, sections.getSections());
    }
}
