package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.entity.SectionEntity;

public interface SectionDao extends UpdateDao<SectionEntity> {

    List<SectionEntity> findSectionsByLineId(Long lineId);

}
