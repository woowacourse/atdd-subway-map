package wooteco.subway.repository.dao;

import java.util.List;
import wooteco.subway.repository.entity.SectionEntity;

public interface SectionDao {

    SectionEntity save(SectionEntity sectionEntity);

    List<SectionEntity> findByLineId(Long lineId);

    List<SectionEntity> findByStationId(Long stationId);

    void update(SectionEntity sectionEntity);

    void deleteByLineIdAndStationId(Long lineId, Long stationId);
}
