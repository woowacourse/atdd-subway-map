package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Section addSection(final long lineId, final Section section) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.add(section);

        Section updatedSection = sections.findLastInsert();
        if (!updatedSection.equals(section)) {
            sectionDao.update(updatedSection.getId(), updatedSection);
        }
        return sectionDao.save(section);
    }
}
