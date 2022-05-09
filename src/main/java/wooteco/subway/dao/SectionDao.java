package wooteco.subway.dao;

import wooteco.subway.domain.Section;

public interface SectionDao {

    long save(Section section);

    boolean existSectionById(Long id);
}
