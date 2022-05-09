package wooteco.subway.dao;

import wooteco.subway.domain.Section;

public interface SectionDao {

    long save(Section section);

    void delete(Long id);

    boolean existSectionById(Long id);
}
