package wooteco.subway.dao;

import wooteco.subway.domain.Section;

import java.util.List;
import java.util.Optional;

public interface SectionDao {

    Optional<Section> findByLineIdAndId(Long lineId, Long sectionId);

    List<Section> findAllByLineId(Long lineId);

    Optional<Section> findByLineIdAndUpStationId(Section section);

    Optional<Section> findByLineIdAndDownStationId(Section section);

    Section save(Section section);

    void updateDownStationAndDistance(Section section);

    void updateUpStationAndDistance(Section section);

    void updateByLineIdAndDownStationId(Section section);

    void deleteByLineIdAndUpStationId(Section section);

    void deleteByLineIdAndDownStationId(Section section);
}
