package wooteco.subway.dao;

import wooteco.subway.domain.Section;

import java.util.List;
import java.util.Optional;

public interface SectionDao {

    Optional<Section> findByLineIdAndId(Long lineId, Long sectionId);

    List<Section> findAllByLineId(Long lineId);

    Optional<Section> findByLineIdAndUpStationId(Long lineId, Long upStationId);

    Optional<Section> findByLineIdAndDownStationId(Long lineId, Long downStationId);

    Section save(Section section);

    void updateDownStationAndDistance(Section section);

    void updateUpStationAndDistance(Section section);

    void deleteByLineIdAndUpStationId(Long lineId, Long upStationId);

    void deleteByLineIdAndDownStationId(Long lineId, Long downStationId);
}
