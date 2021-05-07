package wooteco.subway.section;

import wooteco.subway.exception.InvalidAddSectionException;

import java.util.List;
import java.util.function.BiPredicate;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section newSection) {
        validate(newSection);
        sections.add(newSection);
    }

    private void validate(Section newSection) {
        validateConnected(newSection, this::isConnected);
        validateConnected(newSection, this::isAlreadyExisted);
    }

    private void validateConnected(Section newSection, BiPredicate<Section, Section> biPredicate) {
        sections.stream()
            .filter(section -> biPredicate.test(section, newSection))
            .findAny()
            .orElseThrow(InvalidAddSectionException::new);
    }

    private boolean isConnected(Section newSection, Section section) {
        return section.isUpStation(newSection.getUpStationId()) ||
            section.isDownStation(newSection.getDownStationId());
    }

    private boolean isAlreadyExisted(Section newSection, Section section) {
        return section.isUpStation(newSection.getUpStationId()) &&
            section.isDownStation(newSection.getDownStationId());
    }
}
