package wooteco.subway.service;

import org.springframework.stereotype.Service;

import wooteco.subway.domain.property.Distance;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.SectionSeries;
import wooteco.subway.dto.request.SectionRequest;
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
        final SectionSeries sectionSeries = new SectionSeries(sectionRepository.findAllSections(lineId));
        final Section newSection = create(
            sectionRequest.getUpStationId(),
            sectionRequest.getDownStationId(),
            sectionRequest.getDistance()
        );
        sectionSeries.add(newSection);
        sectionRepository.persist(lineId, sectionSeries);
    }

    Section create(Long upStationId, Long downStationId, int distance) {
        return new Section(
            stationService.findOne(upStationId),
            stationService.findOne(downStationId),
            new Distance(distance)
        );
    }

    public void delete(Long lineId, Long stationId) {
        final SectionSeries sectionSeries = new SectionSeries(sectionRepository.findAllSections(lineId));
        sectionSeries.remove(stationService.findOne(stationId));
        sectionRepository.persist(lineId, sectionSeries);
    }
}
