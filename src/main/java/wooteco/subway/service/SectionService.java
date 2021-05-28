package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResponse;
import wooteco.subway.exception.SameStationSectionException;
import wooteco.subway.exception.StationNotFoundException;
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

    public SectionResponse createSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationDao
            .findById(sectionRequest.getUpStationId())
            .orElseThrow(StationNotFoundException::new);
        Station downStation = stationDao
            .findById(sectionRequest.getDownStationId())
            .orElseThrow(StationNotFoundException::new);

        Section section = addSection(lineId, upStation, downStation, sectionRequest.getDistance());
        return SectionResponse.of(section);
    }

    public SectionResponse createSection(Long lineId, Long upStationId, Long downStationId,
        int distance) {
        validateSameStationForSection(upStationId, downStationId);

        Station upStation = stationDao.findById(upStationId)
            .orElseThrow(StationNotFoundException::new);
        Station downStation = stationDao.findById(downStationId)
            .orElseThrow(StationNotFoundException::new);

        Section section = new Section(upStation, downStation, distance);
        Sections sections = sectionDao.findByLineId(lineId);

        sections.addSection(section);

        return SectionResponse.of(section);
    }

    private Section addSection(Long lineId, Station upStation, Station downStation, int distance) {
        Section section = new Section(upStation, downStation, distance);
        Sections sections = sectionDao.findByLineId(lineId);

        sections.addSection(section);

        long sectionId = sectionDao.save(lineId, section);
        section.setId(sectionId);
        return section;
    }

    private void validateSameStationForSection(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new SameStationSectionException();
        }
    }
}
