package wooteco.subway.section.dao;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

import java.util.List;
import java.util.Optional;

public interface SectionDao {
    Section create(Section section, Long lineId);

    Sections findSectionsByLineId(Long lineId);

    Section saveModified(Section section, Section affectedSection,
                         Long lineId);

    List<Section> findAdjacentByStationId(Long lineId, Long stationId);

    void removeSections(Long lineId, List<Section> sections);

    void insertSection(Section affectedSection, Long lineId);
}
