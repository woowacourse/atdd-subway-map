package wooteco.subway.section.dao;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

import java.util.List;

public interface SectionDao {
    Section save(Section section, Long lineId);

    Sections findSectionsByLineId(Long lineId);

    void deleteById(Long id);

    List<Section> findSectionContainsStationId(Long lineId, Long stationId);

    void deleteStations(Long lineId, List<Section> sections);

    void insertSection(Section affectedSection, Long lineId);
}
