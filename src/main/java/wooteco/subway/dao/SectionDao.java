package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.SectionEntity;

public interface SectionDao {

    Long save(SectionEntity sectionEntity);

    List<SectionEntity> findByLineId(Long lineId);

    void update(SectionEntity sectionEntity);

    void deleteById(Long id);
}
