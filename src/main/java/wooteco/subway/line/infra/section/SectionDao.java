package wooteco.subway.line.infra.section;

import wooteco.subway.line.domain.section.Section;

import java.util.List;
import java.util.Optional;

public interface SectionDao {
    Section save(Section section);

    Optional<Section> findById(Long id);

    List<Section> findByLineId(Long lineId);

    void delete(Long id);
}
