package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Section addSection(final long lineId, final Section section) {
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.add(section);

        final Section updatedSection = sections.findLastInsert();
        if (!updatedSection.equals(section)) {
            sectionDao.update(updatedSection.getId(), updatedSection);
        }
        return sectionDao.save(section);
    }

    @Transactional
    public void delete(final Long lineId, final Long stationId) {
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        final List<Section> sectionsToDelete = sections.pop(stationId);
        final Optional<Section> mergedSection = sections.findMergedSection(sectionsToDelete);
        sectionDao.deleteAll(sectionsToDelete);
        mergedSection.ifPresent(sectionDao::save);
    }

    @Transactional(readOnly = true)
    public List<Section> getSectionsByLine(final long lineId) {
        return sectionDao.findAllByLineId(lineId);
    }
}
