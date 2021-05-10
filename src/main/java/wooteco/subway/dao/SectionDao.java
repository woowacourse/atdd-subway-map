package wooteco.subway.dao;

import wooteco.subway.domain.Section;

import java.util.List;
import java.util.Optional;

public interface SectionDao {

    Optional<Section> findById(Long id);

    List<Section> findAllByLineId(Long lineId);

    Optional<Section> findSectionByDownStationId(Long downStationId);

    Optional<Section> findSectionByUpStationId(Long upStationId);

    Section save(Section section);

    void updateUpStationToDownStation(Long upStationId, Long downStationId);

    void updateDownStationToUpStation(Long upStationId, Long downStationId);

}
