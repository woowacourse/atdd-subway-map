package wooteco.subway.dao.section;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.section.Section;

public interface SectionDao {

    Section save(Section section);

    Optional<Section> findById(Long id);

    List<Section> findByLineId(Long lineId);

    void update(Section section);

    void deleteById(Long id);
}
