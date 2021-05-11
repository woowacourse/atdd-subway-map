package wooteco.subway.section.dao;

import wooteco.subway.section.Section;

import java.util.List;

public interface SectionDao {
    Section save(Section section);

    List<Section> findAllByLineId(Long id);

    void updateUpStation(Section section, Long downStationId);

    void updateDownStation(Section section, Long upStationId);

    void deleteByLineIdAndUpStationId(Long lineId, Long stationId);

    void deleteByLineIdAndDownStationId(Long lineId, Long stationId);

    void deleteBySection(Section section);
}
