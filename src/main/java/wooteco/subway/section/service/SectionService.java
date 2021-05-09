package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.repository.SectionRepository;

@Service
@Transactional
public class SectionService {
    private final SectionRepository sectionRepository;

    public SectionService(final SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public void save(final Section section) {
        if (sectionRepository.doesSectionExist(section)) {
            throw new DuplicateSectionException();
        }
        sectionRepository.save(section);
    }
}
