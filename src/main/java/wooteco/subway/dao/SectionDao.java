package wooteco.subway.dao;

import wooteco.subway.domain.Section;

public interface SectionDao {

    Section save(Section section);

    void deleteById(Long id);

    Section findById(Long id);
}
