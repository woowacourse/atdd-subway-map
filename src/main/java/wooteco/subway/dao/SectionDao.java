package wooteco.subway.dao;

import java.util.Optional;
import wooteco.subway.domain.Section;

public interface SectionDao {

    Section save(Section section);

    void deleteById(Long id);

    Optional<Section> findById(Long id);

    Optional<Object> findAllByLineId(Long lineId);
}
