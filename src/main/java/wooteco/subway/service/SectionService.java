package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResponse;
import wooteco.subway.exception.StationNotFoundException;
import wooteco.subway.repository.SectionDao;
import wooteco.subway.repository.StationDao;

@Service
public class SectionService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionService(StationDao stationDao, SectionDao sectionDao) {
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

        Section section = new Section(upStation, downStation, sectionRequest.getDistance());
        long sectionId = sectionDao.save(lineId, section);
        section.setId(sectionId);
        return SectionResponse.of(section);
    }
}
