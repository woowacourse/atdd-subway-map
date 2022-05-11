package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Section save(Section section);

    List<Section> findByLineId(long lineId);

    int update(List<Section> sections);

    int delete(Section section);
}
