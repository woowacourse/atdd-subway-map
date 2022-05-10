package wooteco.subway.repository.dao;

import java.util.Optional;
import wooteco.subway.repository.entity.SectionEntity;

public interface SectionDao {
    SectionEntity save(final SectionEntity sectionEntity);

    Optional<SectionEntity> findById(final Long id);
}
