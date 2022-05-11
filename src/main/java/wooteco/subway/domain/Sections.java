package wooteco.subway.domain;

import java.util.List;
import java.util.Optional;

public class Sections {
    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public void validate(final Section section) {

    }

    public boolean isLastStation(final Section newSection) {
        return sections.stream()
                .anyMatch(section -> (section.isConnected(newSection) && isRightLastSection(section)) ||
                        (newSection.isConnected(section) && isLeftLastSection(section)));
    }

    private boolean isLeftLastSection(final Section comparedSection) {
        return sections.stream()
                .noneMatch(section -> section.isConnected(comparedSection));
    }

    private boolean isRightLastSection(final Section comparedSection) {
        return sections.stream()
                .noneMatch(comparedSection::isConnected);
    }

    public boolean matchUpStationId(final Section comparedSection) {
        return sections.stream()
                .anyMatch(comparedSection::equalsUpStation);
    }

    public boolean matchDownStationId(final Section comparedSection) {
        return sections.stream()
                .anyMatch(comparedSection::equalsDownStation);
    }
}
