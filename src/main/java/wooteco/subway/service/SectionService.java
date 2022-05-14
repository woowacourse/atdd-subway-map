package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.domain.Distance;
import wooteco.subway.domain.RemoveSections;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionSeries;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.repository.SectionRepository;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public SectionService(SectionRepository sectionRepository, StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public void enroll(Long lineId, SectionRequest sectionRequest) {
        final SectionSeries sectionSeries = new SectionSeries(sectionRepository.readAllSections(lineId));
        final Section newSection = create(
            sectionRequest.getUpStationId(),
            sectionRequest.getDownStationId(),
            sectionRequest.getDistance()
        );
        sectionSeries.add(newSection);
        sectionRepository.persist(lineId, sectionSeries);
    }

    public Section create(Long upStationId, Long downStationId, int distance) {
        return new Section(
            stationService.findOne(upStationId),
            stationService.findOne(downStationId),
            new Distance(distance)
        );
    }

    public void delete(Long lineId, Long stationId) {
        final SectionSeries sectionSeries = new SectionSeries(sectionRepository.readAllSections(lineId));
        sectionSeries.remove(stationId);
        sectionRepository.persist(lineId, sectionSeries);
    }
}
