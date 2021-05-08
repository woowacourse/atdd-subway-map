package wooteco.subway.dao.section;

import wooteco.subway.dao.entity.SectionEntity;

import java.util.List;
import java.util.Optional;

public interface SectionDao {
    SectionEntity save(SectionEntity sectionEntity);

    List<SectionEntity> findAll();

    Optional<SectionEntity> findById(Long id);

    void delete(Long id);
}
