package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.domain.section.Distance;
import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.repository.SectionRepository;
import wooteco.subway.line.service.dto.SectionSaveDto;

@Service
public class SectionService {
    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public void save(Long lineId, SectionSaveDto sectionSaveDto) {
        Section section = new Section(lineId, sectionSaveDto.getUpStationId(),
                sectionSaveDto.getDownStationId(), new Distance(sectionSaveDto.getDistance()));
        sectionRepository.save(section);
    }
}
