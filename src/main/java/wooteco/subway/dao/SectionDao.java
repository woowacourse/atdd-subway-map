package wooteco.subway.dao;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public interface SectionDao {
    void save(Section section, Long lineId);

    void save(Sections sections, Long lineId);

    void delete(Section deletedSection);

    void deleteByLine(Long lineId);
}
