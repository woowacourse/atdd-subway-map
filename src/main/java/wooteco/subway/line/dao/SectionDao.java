package wooteco.subway.line.dao;

import wooteco.subway.line.domain.Section;
import wooteco.subway.line.dto.SectionEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SectionDao {
    Section save(Section section);

    List<Section> findAll();

    Optional<Section> findById(Long id);

    void delete(Long id);

    List<Section> findAllByLineId(Long lineId);

    Map<Long, Long> findUpDownStationIdByLineId(Long lineId);
}
