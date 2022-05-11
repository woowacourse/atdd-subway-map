package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import wooteco.subway.exception.section.DuplicatedSectionException;
import wooteco.subway.exception.section.LongerSectionDistanceException;
import wooteco.subway.exception.section.NonExistenceStationsSectionException;

public class Sections {

    private final List<Section> values;

    public Sections(final List<Section> values) {
        this.values = sort(values);
    }

    private List<Section> sort(List<Section> sections) {
        List<Section> sorted = new ArrayList<>();
        Section upSection = findUpSection(sections);
        sorted.add(upSection);
        while (sorted.size() != sections.size()) {
            upSection = upSection.findNextSection(sections);
            sorted.add(upSection);
        }
        return sorted;
    }

    private Section findUpSection(List<Section> sections) {
        return sections.stream()
                .filter(section -> section.isUpSection(sections))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    public Sections connect(Section newSection) {
        validate(newSection);
        return connectNewSection(newSection);
    }

    private void validate(Section newSection) {
        boolean upStationExistence = values.stream()
                .anyMatch(it -> it.hasSameUpStationOf(newSection));
        boolean downStationExistence = values.stream()
                .anyMatch(it -> it.hasSameDownStationOf(newSection));
        validateDuplicatedSection(upStationExistence, downStationExistence);
        validateNonExistenceStation(upStationExistence, downStationExistence);
    }

    private Sections connectNewSection(Section newSection) {
        Optional<Section> standardSection = values.stream()
                .filter(it -> it.hasSameUpStation(newSection) || it.hasSameDownStation(newSection))
                .findAny();
        return standardSection.map(section -> connectBetween(newSection, section))
                .orElseGet(() -> connectBackOrForth(newSection));
    }

    private Sections connectBackOrForth(final Section newSection) {
        List<Section> changed = new ArrayList<>(values);
        changed.add(newSection);
        return new Sections(changed);
    }

    private Sections connectBetween(Section newSection, Section standardSection) {
        validateDistance(newSection, standardSection);
        List<Section> changed = new ArrayList<>(values);
        changed.set(changed.indexOf(standardSection), standardSection.changeSection(newSection));
        changed.add(newSection);
        return new Sections(changed);
    }

    private void validateDuplicatedSection(final boolean upStationExistence, final boolean downStationExistence) {
        if (upStationExistence && downStationExistence) {
            throw DuplicatedSectionException.getInstance();
        }
    }

    private void validateNonExistenceStation(final boolean upStationExistence, final boolean downStationExistence) {
        if (!upStationExistence == !downStationExistence) {
            throw NonExistenceStationsSectionException.getInstance();
        }
    }

    private void validateDistance(final Section section, final Section standardSection) {
        if (section.hasHigherDistance(standardSection)) {
            throw LongerSectionDistanceException.getInstance();
        }
    }

    public List<Section> findDifferentSections(Sections another) {
        List<Section> sections = new ArrayList<>(values);
        sections.removeAll(another.values);
        return sections;
    }

    public Long findUpStationId() {
        return values.get(0).getUpStationId();
    }

    public Long findDownStationId() {
        int lastIndex = values.size() - 1;
        return values.get(lastIndex).getDownStationId();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Sections sections = (Sections) o;
        return Objects.equals(values, sections.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
