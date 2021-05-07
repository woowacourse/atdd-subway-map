package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import wooteco.subway.exception.section.DuplicatedSectionException;
import wooteco.subway.exception.section.InvalidSectionException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sections {

    private static final int SECTION_LIMIT = 1;
    private static final int FIRST_ELEMENT = 0;
    private final List<Section> sections;

    public static Sections from(Section... sections) {
        return from(new ArrayList<>(Arrays.asList(sections)));
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> stations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toList());
    }

    public Optional<Section> affectedSection(Section newSection) {
        List<Section> collect = sections.stream()
                .filter(originalSection -> isAdjacentSection(newSection, originalSection))
                .collect(Collectors.toList());

        if (collect.size() != SECTION_LIMIT) {
            throw new InvalidSectionException();
        }

        final Section originalSection = collect.get(FIRST_ELEMENT);
        return updateSection(originalSection, newSection);
    }

    private boolean isAdjacentSection(Section newSection, Section originalSection) {
        if (originalSection.isSameSection(newSection)) {
            throw new DuplicatedSectionException();
        }

        return originalSection.isUpStation(newSection.getUpStation()) ||
                originalSection.isDownStation(newSection.getDownStation()) ||
                originalSection.isUpStation(newSection.getDownStation()) ||
                originalSection.isDownStation(newSection.getUpStation());
    }

    private Optional<Section> updateSection(Section originalSection, Section newSection) {

        final Station upStation = newSection.getUpStation();
        final Station downStation = newSection.getDownStation();

        if (originalSection.isUpStation(upStation)) {
            originalSection.updateUpStation(newSection);
            return Optional.of(originalSection);
        }

        if (originalSection.isDownStation(downStation)) {
            originalSection.updateDownStation(newSection);
            return Optional.of(originalSection);
        }

        return Optional.empty();
    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Section> sections() {
        return Collections.unmodifiableList(sections);
    }
}
