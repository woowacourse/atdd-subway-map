package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.infra.repository.SectionRepository;
import wooteco.subway.service.dto.SectionServiceRequest;

@Service
@Transactional
public class SpringSectionService implements SectionService {

    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public SpringSectionService(SectionRepository sectionRepository, StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    @Override
    public void save(Long lineId, SectionServiceRequest request) {
        final Sections sections = sectionRepository.findByLineId(lineId);
        final Section section = new Section(lineId, stationService.findById(request.getUpStationId()),
                stationService.findById(request.getDownStationId()),
                request.getDistance());
        sections.add(section);

        sectionRepository.save(sections);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Sections> findAll() {
        return sectionRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Sections findByLineId(Long lineId) {
        return sectionRepository.findByLineId(lineId);
    }

    @Override
    public void deleteByLineIdAndStationId(Long lineId, Long stationId) {
        final Sections sections = findByLineId(lineId);
        sections.remove(lineId, stationId);
        sectionRepository.save(sections);
    }
}
