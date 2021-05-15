package wooteco.subway.line.domain;

import java.util.List;
import java.util.Optional;

public interface SectionDao {
    Section save(Section section);

    void delete(Long id);

    List<Section> findByLineId(Long id);

    Optional<Section> findByLineIdWithUpStationId(Long lineId, Long stationId);

    void deleteByLineIdWithUpStationId(Long lineId, Long upStationId);

    Optional<Section> findByLineIdWithDownStationId(Long lineId, Long downStationId);

    void deleteByLineIdWithDownStationId(Long lineId, Long downStationId);

    void deleteByLineId(Long lineId);

    void batchInsert(List<Section> sortedSections);
}
