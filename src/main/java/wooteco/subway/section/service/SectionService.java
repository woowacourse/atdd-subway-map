package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.section.repository.SectionRepository;

@Service
public class SectionService {
    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }
}
