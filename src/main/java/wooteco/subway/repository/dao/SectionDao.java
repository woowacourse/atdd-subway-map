package wooteco.subway.repository.dao;

import java.util.List;
import wooteco.subway.repository.entity.SectionEntity;

public interface SectionDao {
    SectionEntity save(SectionEntity section);

    List<SectionEntity> findByLineId(Long lineId);

    int deleteById(Long id);

    int deleteByLineId(Long lineId);

    int update(SectionEntity sections);

    int saveAll(List<SectionEntity> entities);

    List<SectionEntity> findByStationId(Long lineId, Long stationId);
}
