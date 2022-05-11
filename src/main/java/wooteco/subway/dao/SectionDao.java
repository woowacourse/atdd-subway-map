package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.SectionEntity;

public interface SectionDao {

    Long save(Long lineId, Long upStationId, Long downStationId, int distance);

    List<SectionEntity> findByLineId(Long lineId);

    void update(SectionEntity sectionEntity);
}
