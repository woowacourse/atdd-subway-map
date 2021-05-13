package wooteco.subway.section.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.section.domain.Section;

import java.util.Optional;

@Repository
public interface SectionRepository {
    Section save(Section section);

    Optional<Section> findById(Long id);
}
