package wooteco.subway.dao;

import wooteco.subway.domain.Section;

public interface SectionDao {
    void save(Section section, Long id);

    void deleteByLine(Long lineId);
}
