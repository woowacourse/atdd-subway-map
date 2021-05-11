package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.section.dao.JDBCSectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;

@Service
public class SectionService {

    private final JDBCSectionDao JDBCSectionDao;

    public SectionService(JDBCSectionDao JDBCSectionDao) {
        this.JDBCSectionDao = JDBCSectionDao;
    }

    public Section save(Section section) {
        return JDBCSectionDao.save(section);
    }

    public Sections findByLineId(Long lineId) {
        return JDBCSectionDao.findByLineId(lineId);
    }

    public void addSection(Sections sections, Section section) {
        Section newSection = sections.addSection(section);
    }
}
