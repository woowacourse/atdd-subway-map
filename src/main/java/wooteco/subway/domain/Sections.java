package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import wooteco.subway.exception.section.DuplicatedSectionException;
import wooteco.subway.exception.section.SectionCycleException;
import wooteco.subway.exception.section.SectionHasSameUpAndDownException;
import wooteco.subway.exception.section.SectionInternalRemovableConflictException;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sections {

    private static final int FIRST_ELEMENT = 0;
    private static final int SECOND_ELEMENT = 1;
    private static final int EXPECTED_REMOVE_SITUATION = 2;
    private static final int TWO_ADJACENT_SECTIONS = 2;
    private final List<Section> sections;

    public static Sections create(Section... sections) {
        return create(new ArrayList<>(Arrays.asList(sections)));
    }

    public static Sections create(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> convertToSortedStations() {
        Deque<Station> result = new ArrayDeque<>();
        Map<Station, Station> upStationToFindDown = new HashMap<>();
        Map<Station, Station> downStationToFindUp = new HashMap<>();

        for (Section section : sections) {
            upStationToFindDown.put(section.getUpStation(), section.getDownStation());
            downStationToFindUp.put(section.getDownStation(), section.getUpStation());
        }
        Station station = sections.get(FIRST_ELEMENT).getUpStation();
        result.add(station);

        while (downStationToFindUp.containsKey(result.peekFirst())) {
            Station current = result.peekFirst();
            result.addFirst(downStationToFindUp.get(current));
        }
        while (upStationToFindDown.containsKey(result.peekLast())) {
            Station current = result.peekLast();
            result.addLast(upStationToFindDown.get(current));
        }

        return new ArrayList<>(result);
    }

    private Section modifyAdjacent(Section newSection) {
        List<Section> collect = sections.stream()
                .filter(originalSection -> originalSection.isAdjacent(newSection))
                .collect(Collectors.toList());
        if (isMiddleSection(newSection, collect)) {
            return updateSection(collect.get(SECOND_ELEMENT), newSection);
        }
        final Section originalSection = collect.get(FIRST_ELEMENT);
        return updateSection(originalSection, newSection);
    }

    private boolean isMiddleSection(Section newSection, List<Section> sections) {
        return sections.size() == TWO_ADJACENT_SECTIONS &&
                sections.stream()
                        .anyMatch(section -> section.isUpStation(newSection.getUpStation()));
    }

    private boolean isCycleSection(Section newSection, List<Section> collect) {
        return collect.stream().anyMatch(section -> section.isUpStation(newSection.getUpStation())) &&
                collect.stream().anyMatch(section -> section.isDownStation(newSection.getDownStation()));
    }

    private Section updateSection(Section originalSection, Section newSection) {
        return originalSection.updateByNewSection(newSection);
    }

    public Section addAndThenGetModifiedAdjacent(Section section) {
        validateAddable(section);
        Section modifiedSection = modifyAdjacent(section);
        sections.add(section);

        return modifiedSection;
    }

    private void validateAddable(Section target) {
        if (sections.stream().anyMatch(section -> section.isSameOrReversed(target))) {
            throw new DuplicatedSectionException();
        }

        if (isCycleSection(target, sections)) {
            throw new SectionCycleException();
        }

        //todo: 끊겨있는 구간 추가
    }

    public List<Section> sections() {
        return Collections.unmodifiableList(sections);
    }

    public Section mergeTwoIntoOne(Station station) {
        validateIsSizeTwo(sections);

        Section section = sections.get(FIRST_ELEMENT);
        int distance = sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
        if (section.isUpStation(station)) {
            Station downStation = section.getDownStation();
            Station upStation = sections.get(SECOND_ELEMENT).getUpStation();
            return Section.create(upStation, downStation, distance);
        }
        Station upStation = section.getUpStation();
        Station downStation = sections.get(SECOND_ELEMENT).getDownStation();
        return Section.create(upStation, downStation, distance);
    }

    private void validateIsSizeTwo(List<Section> sections) {
        if (sections.size() != EXPECTED_REMOVE_SITUATION) {
            throw new SectionInternalRemovableConflictException();
        }
    }

    public boolean hasSize(int size) {
        return sections.size() == size;
    }
}
