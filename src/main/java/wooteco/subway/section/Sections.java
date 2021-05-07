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
        validateConnected(newSection, this::isNotExisted);
        validateDistance(newSection);
    }

    private void validateConnected(Section newSection, BiPredicate<Section, Section> biPredicate) {
        sections.stream()
            .filter(section -> biPredicate.test(section, newSection))
            .findAny()
            .orElseThrow(InvalidAddSectionException::new);
    }

    private boolean isConnected(Section newSection, Section section) {
        return isEndpoint(newSection, section) || isIntermediate(newSection, section);
    }

    private boolean isEndpoint(Section newSection, Section section) {
        return section.isUpStation(newSection.getDownStationId()) ||
                section.isDownStation(newSection.getUpStationId());
    }

    private boolean isNotExisted(Section newSection, Section section) {
        return !(section.isUpStation(newSection.getUpStationId()) &&
            section.isDownStation(newSection.getDownStationId()));
    }

    private void validateDistance(Section newSection) {
        sections.stream()
            .filter(section -> section.isUpStation(newSection.getUpStationId()))
            .findAny()
            .ifPresent(section -> isValidDistance(newSection, section));
    }

    private void isValidDistance(Section newSection, Section section) {
        if (section.isSameOrLongDistance(newSection)) {
            throw new InvalidAddSectionException();
        }
    }

    private boolean isIntermediate(Section newSection, Section section) {
        return section.isUpStation(newSection.getUpStationId()) ||
                section.isDownStation(newSection.getDownStationId());
    }
}
