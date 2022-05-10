package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public interface SectionDao {

    Section save(Section section);

    Section findById(Long id);

    Sections findByLineId(Long lineId);

    int updateSection(Section updateSection);

    List<Section> findAll();
}
