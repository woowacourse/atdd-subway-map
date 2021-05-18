package wooteco.subway.line.domain.section.validate.delete;

import wooteco.subway.line.domain.section.Sections;

public interface SectionDeleteValidator {
    void validateDeleteSection(Sections sections, Long stationId);
}
