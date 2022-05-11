package wooteco.subway.repository.dao;

import java.util.List;

import wooteco.subway.repository.dao.entity.SectionEntity;

public interface SectionDao {

    Long save(SectionEntity section);

    List<SectionEntity> findAllByLineId(Long lineId);

    void remove(Long id);
}
