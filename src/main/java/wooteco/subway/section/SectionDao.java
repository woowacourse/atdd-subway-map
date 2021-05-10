package wooteco.subway.section;

import java.util.List;
import java.util.Optional;

public interface SectionDao {
    Section save(Long lineId, Section section);

    Optional<Section> findBySameUpOrDownId(Long lineId, Section newSection);

    void updateUpStation(Long id, Long upStationId, int distance);

    void updateDownStation(Long id, Long downStationId, int distance);

    List<Section> findByStation(Long lineId, Long stationId);

    void delete(Long id);

    List<Section> findByLineId(Long lineId);
}
