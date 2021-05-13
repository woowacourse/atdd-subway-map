package wooteco.subway.section.dao;

import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;

import java.util.List;

public interface SectionDao {
    Section save(Section section);

    Section findById(Long id);

    Sections findByLineId(Long lineId);

    void update(Section section);

    void deleteAll();

    void delete(Section newSection);

    List<Section> findByLineIdAndStationId(Long lineId, Long stationId);

    boolean canDelete(Long lineId);

    boolean existsStationInSection(Long stationId);

    void deleteSections(Long lineId);
}