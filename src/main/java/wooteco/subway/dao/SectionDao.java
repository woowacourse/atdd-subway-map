package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.entity.SectionEntity;

public interface SectionDao {

    Long save(SectionEntity section);

    Long deleteById(Long id);

    List<SectionEntity> findSectionsByLineId(Long lineId);

    Long update(SectionEntity entity);

}
