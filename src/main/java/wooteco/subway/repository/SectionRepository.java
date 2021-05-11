package wooteco.subway.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.domain.section.Section;

@Repository
public class SectionRepository {

    private final SectionDao sectionDao;

    public SectionRepository(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(Section section) {
        return sectionDao.save(section);
    }

    public List<Section> findByLineId(Long lineId) {
        return sectionDao.findByLineId(lineId);
    }

    public void update(Section section) {
        sectionDao.update(section);
    }

    public void delete(Section section) {
        sectionDao.deleteById(section.getId());
    }
}
