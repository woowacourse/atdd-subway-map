package wooteco.subway.section.dao;

import wooteco.subway.domain.Section;

import java.util.List;

public interface SectionDao {
    Section create(Section section, Long lineId);

    List<SectionTable> findAllByLineId(Long targetLineId);

    void updateModified(Section section);

    void remove(Long lineId, Long upStationId, Long downStationId);

    boolean isLast(Long lineId);
}
