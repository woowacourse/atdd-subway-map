package wooteco.subway.section.dao;

import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;

public interface SectionDao {
    Section save(Section section);

    Section findById(Long id);

    Sections findByLineId(Long lineId);

    void update(Section section);

    void deleteAll();
}