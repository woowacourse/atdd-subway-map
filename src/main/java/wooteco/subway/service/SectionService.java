package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.service.dto.SectionDto;

import java.util.List;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public SectionService(SectionRepository sectionRepository, StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public long createSection(SectionDto sectionDto, long lineId) {
        Station upStation = stationService.findById(sectionDto.getUpStationId());
        Station downStation = stationService.findById(sectionDto.getDownStationId());
        Section section = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(sectionDto.getDistance())
                .lineId(lineId)
                .build();
        return sectionRepository.save(section);
    }

    public void addSection(SectionDto sectionDto, long lineId) {
        Station upStation = stationService.findById(sectionDto.getUpStationId());
        Station downStation = stationService.findById(sectionDto.getDownStationId());
        Section requestSection = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(sectionDto.getDistance())
                .lineId(lineId)
                .build();
        Sections currentSections = new Sections(sectionRepository.findAllByLineId(lineId));
        splitAndUpdateSections(currentSections, requestSection);
    }

    private void splitAndUpdateSections(Sections currentSections, Section requestSection) {
        if (currentSections.canExtendEndSection(requestSection)) {
            sectionRepository.save(requestSection);
            return;
        }
        Section splitSection = currentSections.splitLongerSectionAfterAdding(requestSection);
        sectionRepository.update(splitSection);
        sectionRepository.save(requestSection);
    }

    public void deleteSection(long lineId, long stationId) {
        Sections currentSections = new Sections(sectionRepository.findAllByLineId(lineId));
        if (currentSections.isNotDeletable()) {
            throw new SubwayException(ExceptionStatus.INVALID_SECTION);
        }
        List<Section> sectionsAroundStation = sectionRepository.findAllByStationId(stationId);
        deleteAndAppendSections(currentSections, sectionsAroundStation);
    }

    private void deleteAndAppendSections(Sections currentSections, List<Section> sectionsAroundStation) {
        Sections removableSections = new Sections(sectionsAroundStation);
        if (currentSections.canDeleteEndSection(removableSections)) {
            Section removableSection = sectionsAroundStation.get(0);
            sectionRepository.delete(removableSection);
            return;
        }
        Section appendedSection = removableSections.append();
        sectionRepository.save(appendedSection);
        sectionsAroundStation.forEach(sectionRepository::delete);
    }

    public void deleteAllByLineId(long lineId) {
        sectionRepository.deleteAllByLineId(lineId);
    }
}
