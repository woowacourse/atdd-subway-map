package wooteco.subway.domain;

import java.util.List;
import wooteco.subway.exception.section.InvalidSectionOnLineException;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void validateSavable(final Section section) {
        if (existedUpStation(section) == existedDownStation(section)) {
            throw new InvalidSectionOnLineException();
        }
    }

    private boolean existedUpStation(final Section section) {
        return sections.stream()
            .map(Section::getUpStationId)
            .anyMatch(id -> id.equals(section.getUpStationId()) ^ id.equals(section.getDownStationId()));
    }

    private boolean existedDownStation(final Section section) {
        return sections.stream()
            .map(Section::getDownStationId)
            .anyMatch(id -> id.equals(section.getUpStationId()) ^ id.equals(section.getDownStationId()));
    }
}
