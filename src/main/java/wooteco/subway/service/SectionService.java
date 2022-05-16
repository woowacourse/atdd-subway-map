package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.service.dto.SectionDeleteRequest;
import wooteco.subway.service.dto.SectionSaveRequest;

@Service
@Transactional
public class SectionService {

    private final StationService stationService;
    private final SectionRepository sectionRepository;

    public SectionService(StationService stationService, SectionRepository sectionRepository) {
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    public void save(SectionSaveRequest request) {
        Section sectionForSave = new Section(request.getLineId(),
                stationService.findById(request.getUpStationId()),
                stationService.findById(request.getDownStationId()), request.getDistance());
        Sections sections = new Sections(findByLineId(request.getLineId()));
        sections.add(sectionForSave);

        commitRepository(request.getLineId(), sections.getValue());
    }

    @Transactional(readOnly = true)
    public List<Section> findByLineId(Long lineId) {
        return sectionRepository.findByLineId(lineId);
    }

    public void delete(SectionDeleteRequest request) {
        List<Section> sectionList = findByLineId(request.getLineId());
        Sections sections = new Sections(sectionList);
        Station stationForDelete = stationService.findById(request.getStationId());
        sections.deleteNearBy(stationForDelete);
        commitRepository(request.getLineId(), sections.getValue());
    }

    private void commitRepository(Long lineId, List<Section> sections) {
        sectionRepository.deleteByLineId(lineId);
        sectionRepository.saveAll(sections);
    }
}
