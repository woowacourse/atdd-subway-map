package wooteco.subway.dao;

import wooteco.subway.domain.Section;

public interface SectionDao {
    Long save(Section section, Long lineId);

    boolean update(Section value);

    boolean delete(Long deletedSectionId);
}
