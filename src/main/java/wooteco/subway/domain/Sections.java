package wooteco.subway.domain;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import wooteco.subway.exception.CannotConnectSectionException;
import wooteco.subway.exception.SectionDuplicateException;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(final Section section) {
        validateDuplicateSection(section);
        validateConnectSection(section);
        if (findNearbySection(section).isPresent()) {
            updateSection(findNearbySection(section).get(), section);
        }
        sections.add(section);
    }

    private void validateDuplicateSection(final Section checkSection) {
        boolean isDuplicate = sections.stream()
                .anyMatch(section -> section.isDuplicateSection(checkSection));
        if (isDuplicate) {
            throw new SectionDuplicateException();
        }
    }

    private void validateConnectSection(final Section checkSection) {
        sections.stream()
                .filter(section -> section.hasSectionToConnect(checkSection))
                .findFirst()
                .orElseThrow(CannotConnectSectionException::new);
    }

    private Optional<Section> findNearbySection(final Section newSection) {
        return sections.stream()
                .filter(section -> section.getUpStation().isSameStation(newSection.getUpStation())
                        || section.getDownStation().isSameStation(newSection.getDownStation()))
                .findFirst();
    }

    private void updateSection(final Section foundSection, final Section newSection) {
        final Station upStation = foundSection.getUpStation();
        final Station downStation = foundSection.getDownStation();

        if (upStation.isSameStation(newSection.getUpStation())) {
            foundSection.updateSection(newSection.getDownStation(), downStation, newSection.getDistance());
        }
        if (downStation.isSameStation(newSection.getDownStation())) {
            foundSection.updateSection(newSection.getUpStation(), downStation, newSection.getDistance());
        }
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
