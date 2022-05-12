package wooteco.subway.domain;

import wooteco.subway.exception.IllegalSectionCreatedException;
import wooteco.subway.exception.IllegalSectionDeleteException;

import java.util.List;

public class Sections {
    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public void validatePossibleSection(final Section section) {
        if (!containsStationInSectionsAsXOR(section)) {
            throw new IllegalSectionCreatedException();
        }
    }

    private boolean containsStationInSectionsAsXOR(final Section newSection) {
        final boolean containsUpStation = sections.stream()
                .anyMatch(section -> section.containsUpStationIdBy(newSection));
        final boolean containsDownStation = sections.stream()
                .anyMatch(section -> section.containsDownStationIdBy(newSection));
        return containsUpStation ^ containsDownStation;
    }

    private boolean noneMatchLeftSection(final Section comparedSection) {
        return sections.stream()
                .noneMatch(section -> section.isConnected(comparedSection));
    }

    private boolean noneMatchRightSection(final Section comparedSection) {
        return sections.stream()
                .noneMatch(comparedSection::isConnected);
    }

    public Section integrateTwoSections() {
        if (!hasTwoSection()) {
            throw new IllegalSectionDeleteException();
        }
        final Section firstSection = sections.get(0);
        final Section secondSection = sections.get(1);
        return firstSection.integrate(secondSection);
    }

    public boolean isLastStation(final Section newSection) {
        return sections.stream()
                .anyMatch(section -> isLeftLastStation(newSection, section) ||
                        isRightLastStation(newSection, section));
    }

    private boolean isLeftLastStation(final Section newSection, final Section section) {
        return section.isConnected(newSection) && noneMatchRightSection(section);
    }

    private boolean isRightLastStation(final Section newSection, final Section section) {
        return newSection.isConnected(section) && noneMatchLeftSection(section);
    }

    public boolean matchUpStationId(final Section section) {
        return sections.stream()
                .anyMatch(section::equalsUpStation);
    }

    public boolean matchDownStationId(final Section section) {
        return sections.stream()
                .anyMatch(section::equalsDownStation);
    }

    public boolean hasOneSection() {
        return sections.size() == 1;
    }

    public boolean hasTwoSection() {
        return sections.size() == 2;
    }
}
