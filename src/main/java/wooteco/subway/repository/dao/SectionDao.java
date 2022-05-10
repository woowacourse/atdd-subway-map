package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.repository.entity.SectionEntity;

public interface SectionDao {
    SectionEntity save(final SectionEntity sectionEntity);

    Optional<SectionEntity> findById(final Long id);

    List<SectionEntity> findByLineId(final Long lineId);

    void deleteById(final Long id);
}
