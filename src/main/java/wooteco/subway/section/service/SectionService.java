package wooteco.subway.section.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.NotExistLineException;
import wooteco.subway.exception.NotExistStationException;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.station.service.StationService;

@Transactional
@Service
public class SectionService {

    private final StationService stationService;
    private final SectionRepository sectionRepository;

    public SectionService(StationService stationService,
        SectionRepository sectionRepository) {
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    public Section create(Long lineId, SectionRequest sectionRequest) {
        Section newSection = section(lineId, sectionRequest);
        return sectionRepository.save(newSection);
    }

    @Transactional(readOnly = true)
    public List<Section> findAllByLineId(Long id) {
        List<Section> sections = sectionRepository.findAllByLineId(id);
        if (sections.isEmpty()) {
            throw new NotExistLineException();
        }
        return sections;
    }

    public void delete(Long lineId, SectionRequest sectionRequest) {
        Section newSection = section(lineId, sectionRequest);
        if (sectionRepository.delete(newSection) == 0) {
            throw new NotExistStationException();
        }
    }

    private Section section(Long lineId, SectionRequest sectionRequest) {
        return new Section(sectionRequest.getId(), lineId,
            stationService.findById(sectionRequest.getUpStationId()),
            stationService.findById(sectionRequest.getDownStationId()),
            sectionRequest.getDistance());
    }

}
