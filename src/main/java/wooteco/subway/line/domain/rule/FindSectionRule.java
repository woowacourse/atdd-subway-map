package wooteco.subway.line.domain.rule;

import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;

import java.util.Optional;

public interface FindSectionRule {
    Optional<Section> findSection(Sections sections, Section newSection);
}
