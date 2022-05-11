package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public interface SectionDao {

    Section save(Section section);

    int update(Section section);

    Sections findByLineId(Long lineId);

    int delete(Long sectionId);

    int deleteByIds(List<Long> sectionIds);
}
