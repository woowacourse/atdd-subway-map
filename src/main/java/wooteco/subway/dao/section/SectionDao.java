package wooteco.subway.dao.section;

import java.util.List;
import wooteco.subway.domain.Section;

public interface SectionDao {

    long save(Section section);

    List<Section> findAllByLineId(long lineId);

    int updateSections(List<Section> sections);

    int delete(long sectionId);
}
