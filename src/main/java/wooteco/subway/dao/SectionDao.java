package wooteco.subway.dao;

import wooteco.subway.domain.Section;

import java.util.List;
import java.util.Optional;

public interface SectionDao {

    Optional<Section> findById(Long id);

    List<Section> findAllById(Long id);

    Section save(Section section);
}
