package wooteco.subway.dao;

import wooteco.subway.domain.Section;

import java.util.List;

public interface SectionRepository {
    Section save(long lineId, Section section);

    void saveSections(long lineId, List<Section> sections);

    void deleteSectionsByLineId(long lineId);

    Section findById(long sectionId);

    List<Section> findAllByLineId(long lineId);

    Long getUpStationIdById(long id);

    Long getDownStationIdById(long id);
}
