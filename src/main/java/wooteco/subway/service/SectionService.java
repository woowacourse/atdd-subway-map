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

    public void editSection(long lineId, long upStationId, long downStationId, int distance) {
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);
        Section section = new Section(upStation, downStation, distance, lineId);
        List<Section> sectionList = sectionRepository.findAllByLineId(lineId);
        Sections sections = new Sections(sectionList);
        if (sections.isEndStationExtension(section)) {
            sectionRepository.save(section);
            return;
        }
        Section editableSection = sections.splitLongerSectionAfterAdding(section);
        sectionRepository.update(editableSection);
        sectionRepository.save(section);
    }

    public void delete(long lineId, long stationId) {
        List<Section> sectionListT = sectionRepository.findAllByLineId(lineId);
        Sections sections = new Sections(sectionListT);

        List<Section> sectionList = sectionRepository.findAllByStationId(stationId);
        Sections appendableSections = new Sections(sectionList);
        if (sections.isEndStationReduction(appendableSections)) {
            sectionRepository.delete(sectionList.get(0));
            return;
        }
        Section left = appendableSections.toList().get(0);
        Section right = appendableSections.toList().get(1);
        Section append = left.append(right);
        sectionRepository.save(append);
        sectionList.forEach(sectionRepository::delete);
    }
}
