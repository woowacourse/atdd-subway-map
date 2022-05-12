package wooteco.subway.service;

import org.springframework.stereotype.Service;

import wooteco.subway.domain.Distance;
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
        SectionSeries sectionSeries = sectionRepository.readAllSections(lineId);
        Section newSection = createSection(sectionRequest);
        sectionRepository.create(newSection, lineId);
        sectionSeries.findUpdateSection(newSection).ifPresent(sectionRepository::update);
    }

    public Section createSection(SectionRequest sectionRequest) {
        return new Section(
            stationRepository.findById(sectionRequest.getUpStationId()),
            stationRepository.findById(sectionRequest.getDownStationId()),
            new Distance(sectionRequest.getDistance())
        );
    }

    public void delete(Long lineId, Long stationId) {
        final SectionSeries sectionSeries = sectionRepository.readAllSections(lineId);
        // final List<Section> deleteSections = sectionSeries.findDeleteSections(stationId);

    }
}
