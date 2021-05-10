package wooteco.subway.section.dao;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

import java.util.List;
import java.util.Optional;

public interface SectionDao {
    Section save(Section section, Long lineId);

    Sections findSectionsByLineId(Long lineId);

    void change(Sections sections, Optional<Section> affectedSection);

    Section saveAffectedSections(Section section, Optional<Section> affectedSection,
                                 Long lineId);

    List<Section> findSectionContainsStationId(Long lineId, Long stationId);

    void deleteStations(Long lineId, List<Section> sections);

    void insertSection(Section affectedSection, Long lineId);
}
