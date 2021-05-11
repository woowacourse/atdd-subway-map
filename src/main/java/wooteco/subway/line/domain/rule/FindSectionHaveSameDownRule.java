package wooteco.subway.line.domain.rule;

import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;

import java.util.Optional;

public class FindSectionHaveSameDownRule implements FindSectionRule {
    @Override
    public Optional<Section> findSection(final Sections sections, final Section newSection) {
        return sections.toList().stream()
                .filter(section -> newSection.getDownStationId().equals(section.getDownStationId()))
                .findFirst();
    }
}
