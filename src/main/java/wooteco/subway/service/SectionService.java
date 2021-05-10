package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.SectionRepository;

import java.util.List;

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

    public void addSection(long upStationId, long downStationId, int distance, long lineId) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);
        Section requestSection = new Section(upStation, downStation, distance, lineId);
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
        List<Section> sectionsAroundStation = sectionRepository.findAllByStationId(stationId);
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
}
