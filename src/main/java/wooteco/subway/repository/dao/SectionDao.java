package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.repository.dao.entity.section.SectionEntity;

public interface SectionDao {

    Long save(SectionEntity section);

    List<SectionEntity> findAllByLineId(Long lineId);

    Boolean existsById(Long id);

    Boolean existsByStationId(Long stationId);

    void remove(Long id);
}
