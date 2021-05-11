package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.section.Section;
import wooteco.subway.repository.SectionDao;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section createSection(Section section, Long lineId) {
        long id = sectionDao.save(section, lineId);
        section.setId(id);
        return section;
    }
}
