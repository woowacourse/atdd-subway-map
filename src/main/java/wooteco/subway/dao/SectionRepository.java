package wooteco.subway.dao;

import wooteco.subway.domain.Section;

import java.util.List;

public interface SectionRepository {
    Section save(long lineId, Section section);

    Section findById(long sectionId);

    List<Section> findAllByLineId(long lineId);

    Long getUpStationIdById(long id);

    Long getDownStationIdById(long id);
}
