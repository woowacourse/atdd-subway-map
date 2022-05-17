package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Section;

public interface SectionDao {
    Long save(Section section, Long lineId);

    void update(List<Section> sections);

    boolean delete(Long deletedSectionId);
}
