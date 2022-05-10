package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Long save(Section section) {
        Sections sections = new Sections(sectionDao.findSectionsByLineId(section.getLineId()));
        sections.add(section);
        return sectionDao.save(section);
    }

    public List<Section> findAll() {
        return sectionDao.findAll();
    }
}
