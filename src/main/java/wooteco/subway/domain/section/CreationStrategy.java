package wooteco.subway.domain.section;

import java.util.List;
import java.util.Optional;

public interface CreationStrategy {

    void save(List<Section> sections, Section section);

    Optional<Section> fixOverLappedSection(List<Section> sections, Section section);
}
