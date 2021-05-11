package wooteco.subway.service.section;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.request.SectionRequest;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository,
        StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public void createSection(LineRequest lineRequest, Long lineId) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId());
        Station downStation = stationRepository.findById(lineRequest.getDownStationId());
        Section section = new Section(lineId, upStation, downStation, lineRequest.getDistance());
        sectionRepository.save(section);
    }

    public void deleteSectionsByLineId(Long id) {
        List<Section> sections = sectionRepository.findByLineId(id);
        sections.forEach(section -> sectionRepository.delete(section));
    }

    public List<Section> findByLineId(Long id) {
        return sectionRepository.findByLineId(id);
    }

    public void addSection(Long id, SectionRequest sectionRequest) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId());
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId());
        Section newSection = new Section(id, upStation, downStation, sectionRequest.getDistance());

        Sections sections = new Sections(sectionRepository.findByLineId(id));
        if (sections.canAddToEndSection(newSection)) {
            sectionRepository.save(newSection);
            return;
        }
        sectionRepository.save(newSection);
        Section updateSection = sections.addToBetweenExistedSection(newSection);
        sectionRepository.update(updateSection);
    }
}
