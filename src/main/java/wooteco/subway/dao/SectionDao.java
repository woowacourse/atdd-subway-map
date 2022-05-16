package wooteco.subway.dao;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public interface SectionDao {

    Section save(Long lineId, Section section);

    Sections findById(Long lineId);

    void delete(Long lineId, Section section);
}
