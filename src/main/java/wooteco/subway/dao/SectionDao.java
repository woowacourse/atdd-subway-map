package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.entity.SectionEntity;

public interface SectionDao {

    SectionEntity save(SectionEntity section, Long lineId);

    void deleteById(Long id);

    List<SectionEntity> readSectionsByLineId(Long lineId);

    void update(SectionEntity entity);
}
