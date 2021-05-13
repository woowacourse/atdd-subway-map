package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.section.domain.Distance;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.section.service.dto.SectionSaveDto;

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
