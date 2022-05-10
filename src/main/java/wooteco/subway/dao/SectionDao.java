package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Section save(Section section);

    int deleteById(Long id);

    List<Section> findByLineId(Long id);

    int deleteByLineId(Long lineId);
}
