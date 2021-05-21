package wooteco.subway.section;

import java.util.List;
import java.util.Optional;

public interface SectionDao {
    Section save(Long lineId, Section section);

    Optional<Section> findBySameUpOrDownId(Long lineId, Section newSection);

    List<Section> findByStation(Long lineId, Long stationId);

    List<Section> findByLineId(Long lineId);

    void updateUpStation(Long id, Long upStationId, int distance);

    void updateDownStation(Long id, Long downStationId, int distance);

    void delete(Long id);

    Optional<Section> existsByLineId(Long lineId);
}
