package wooteco.subway.section.dao;

import wooteco.subway.section.Section;

import java.util.List;

public interface SectionDao {
    Section save(Section section);

    List<Section> findAllByLineId(Long id);

    void updateUpStationId(Section section, Long upStationId);

    void updateDownStationId(Section section, Long downStationId);

    void deleteByLineIdAndUpStationId(Long lineId, Long upStationId);

    void deleteByLineIdAndDownStationId(Long lineId, Long downStationId);

    void deleteBySection(Section section);
}
