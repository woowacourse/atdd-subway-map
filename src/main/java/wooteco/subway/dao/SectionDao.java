package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {
    Section save(Section section);

    List<Section> findByLineId(Long lineId);

    int deleteById(Long id);

    int update(Section sections);
}
