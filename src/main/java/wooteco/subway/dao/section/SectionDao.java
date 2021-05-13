package wooteco.subway.dao.section;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

import java.util.List;
import java.util.Map;

public interface SectionDao {
    Section save(Section section, Long lineId);

    Sections findByLineId(Long lineId);

    Map<Long, Sections> findAll();

    void deleteById(Long id);

    List<Section> findContainsStationId(Long lineId, Long stationId);

    void deleteStations(Long lineId, List<Section> sections);

    void insertSection(Section affectedSection, Long lineId);
}
