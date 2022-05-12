package wooteco.subway.dao;

import wooteco.subway.domain.Section;

import java.util.List;
import java.util.Optional;

public interface SectionDao {

    long save(Section section);

    Optional<Section> findById(Long id);

    List<Section> findByLineId(Long lineId);

    long update(Section sectionWithSameUpStation);

    void deleteSection(Section upperSection);
}
