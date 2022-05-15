package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public interface SectionDao {

    Section save(Section section);

    Sections findByLineId(Long lineId);

    int update(Section section);

    int deleteById(Long id);

    int deleteByIds(List<Long> ids);
}
