package wooteco.subway.dao;

import wooteco.subway.domain.Section;

public interface SectionDao {
    Section save(Section section, Long lineId);

    void deleteByLine(Long lineId);
}
