package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.SectionRepository;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public SectionService(SectionRepository sectionRepository, StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public long createSection(long upStationId, long downStationId, int distance, long lineId) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);
        Section section = new Section(upStation, downStation, distance, lineId);
        return sectionRepository.save(section);
    }
}
