package wooteco.subway.line.domain.section.validate.add;

import wooteco.subway.line.domain.section.Section;
import wooteco.subway.line.domain.section.Sections;

public interface SectionAddValidator {
    void validatePossibleToAdd(Sections sections, Section newSection);
}
