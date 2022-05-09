package wooteco.subway.dao;

import wooteco.subway.domain.Section;

public interface SectionDao {

    Section create(Section section);

    void delete(Long id);

    boolean existById(Long id);
}
