package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Section save(Section section);

    int update(Section section);

    List<Section> findByLineId(Long lineId);

    int delete(Long sectionId);

    int deleteByIds(List<Long> sectionIds);
}
