package wooteco.subway.line.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.section.Section;

import java.util.Optional;

@Repository
public interface SectionRepository {
    Section save(Section section);

    Optional<Section> findById(Long id);
}
