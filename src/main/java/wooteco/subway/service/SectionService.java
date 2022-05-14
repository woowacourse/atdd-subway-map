package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.domain.RemoveSections;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionSeries;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.repository.SectionRepository;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public void enroll(Long lineId, SectionRequest sectionRequest) {
        final SectionSeries sectionSeries = new SectionSeries(sectionRepository.readAllSections(lineId));
        final Section newSection = create(sectionRequest.getUpStationId(),
            sectionRequest.getDownStationId(),
            sectionRequest.getDistance());
        final List<Section> dirties = sectionSeries.add(newSection);
        sectionRepository.synchronize(lineId, dirties);
    }

    public Section create(Long upStationId, Long downStationId, int distance) {
        return sectionRepository.toSection(null, upStationId, downStationId, distance);
    }

    public void delete(Long lineId, Long stationId) {
        final SectionSeries sectionSeries = new SectionSeries(sectionRepository.readAllSections(lineId));
        List<Section> dirties = sectionSeries.remove(stationId);
        sectionRepository.synchronize(lineId, dirties);
    }
}
