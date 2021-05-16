package wooteco.subway.line.domain.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.section.Section;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository {
    Section save(Section section);

    Optional<Section> findById(Long id);

    List<Section> findByLineId(Long lineId);

    void remove(Long id);
}
