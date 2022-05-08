package wooteco.subway.dao;

import wooteco.subway.domain.Section;

import java.util.List;
import java.util.Optional;

public interface SectionDao {
    Long save(Section section);

    Optional<Section> findById(Long id);

    List<Section> findByLineId(Long lineId);

    Optional<Section> findBySameUpOrDownStationId(Long lineId, Section section);

    void updateDownStation(Long id, Long downStationId, int newDistance);

    void updateUpStation(Long id, Long upStationId, int newDistance);

    void delete(List<Section> sections);
}
