package wooteco.subway.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.entity.SectionEntity;

@Repository
public class SectionRepository {

    private final SectionDao sectionDao;

    public SectionRepository(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(Section section) {
        SectionEntity savedEntity = sectionDao.save(
                new SectionEntity(section.getId(), section.getLine_id(), section.getUpStationId(),
                        section.getDownStationId(), section.getDistance()));
        return new Section(savedEntity.getId(), savedEntity.getLine_id(), savedEntity.getUpStationId(),
                savedEntity.getDownStationId(), savedEntity.getDistance());
    }

    public Section update(Section section) {
        sectionDao.update(new SectionEntity(section.getId(), section.getLine_id(), section.getUpStationId(),
                section.getDownStationId(), section.getDistance()));
        return section;
    }

    public void deleteById(Long id) {
        sectionDao.deleteById(id);
    }
}
