package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.repository.SectionDao;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public Section createSection(SectionRequest sectionRequest, Long lineId) {
        Section section = new Section(
            stationService.findById(sectionRequest.getUpStationId()),
            stationService.findById(sectionRequest.getDownStationId()),
            sectionRequest.getDistance()
        );
        Long sectionId = sectionDao.save(section, lineId);
        section.setId(sectionId);
        return section;
    }
}
