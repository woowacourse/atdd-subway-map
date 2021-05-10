package wooteco.subway.dao.section;

import wooteco.subway.domain.Section;

public interface SectionDao {

    Section save(Section section, Long lineId);

}
