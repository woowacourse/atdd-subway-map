package wooteco.subway.line.dao;

import wooteco.subway.line.dto.SectionEntity;

import java.util.List;
import java.util.Optional;

public interface SectionDao {
    SectionEntity save(SectionEntity sectionEntity);

    List<SectionEntity> findAll();

    Optional<SectionEntity> findById(Long id);

    void delete(Long id);

    List<SectionEntity> findAllByLineId(Long lineId);
}
