package wooteco.subway.service;

import org.springframework.stereotype.Service;

import wooteco.subway.domain.RemoveSections;
import wooteco.subway.domain.EnrollSections;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionSeries;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public void create(Long lineId, SectionRequest sectionRequest) {
        final SectionSeries sectionSeries = sectionRepository.readAllSections(lineId);
        final Section newSection = sectionRepository.readSection(sectionRequest.getUpStationId(),
            sectionRequest.getDownStationId(),
            sectionRequest.getDistance());
        final EnrollSections enrollSections = sectionSeries.findEnrollSections(newSection);
        sectionRepository.create(lineId, enrollSections.getCreateSection(), enrollSections.getUpdateSection());
    }

    public void delete(Long lineId, Long stationId) {
        final SectionSeries sectionSeries = sectionRepository.readAllSections(lineId);
        RemoveSections removeSections = sectionSeries.findDeleteSections(stationId);
        sectionRepository.delete(removeSections.getDeleteSection(), removeSections.getUpdateSection());
    }
}
