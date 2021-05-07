package wooteco.subway.line.domain;

import java.util.List;
import java.util.Optional;

public interface SectionDao {
    Section save(Section section);

    List<Section> findAll();

    Optional<Section> findById(Long id);

    void delete(Long id);
}
