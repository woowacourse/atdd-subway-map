package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import wooteco.subway.exception.InternalLogicConflictException;
import wooteco.subway.exception.section.SectionCycleException;
import wooteco.subway.exception.section.SectionDuplicatedException;
import wooteco.subway.exception.section.SectionLastRemainedException;
import wooteco.subway.exception.section.SectionUnlinkedException;
import wooteco.subway.exception.station.StationNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sections {
    private static final int FIRST_ELEMENT = 0;
    private static final int IN_BETWEEN = 2;
    private static final int ONLY_END_RELATED = 1;
    private static final int SECOND_ELEMENT = 1;
    private static final int LAST_ONE = 1;

    private List<Section> sections;

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
        setMapToFindStations(upStationToFindDown, downStationToFindUp);

        Station pivotStation = sections.get(FIRST_ELEMENT).getUpStation();
        result.add(pivotStation);
        sortStations(result, upStationToFindDown, downStationToFindUp);

        return new ArrayList<>(result);
    }

    private void sortStations(Deque<Station> result, Map<Station, Station> upStationToFindDown, Map<Station, Station> downStationToFindUp) {
        while (downStationToFindUp.containsKey(result.peekFirst())) {
            Station current = result.peekFirst();
            result.addFirst(downStationToFindUp.get(current));
        }
        while (upStationToFindDown.containsKey(result.peekLast())) {
            Station current = result.peekLast();
            result.addLast(upStationToFindDown.get(current));
        }
    }

    private void setMapToFindStations(Map<Station, Station> upStationToFindDown, Map<Station, Station> downStationToFindUp) {
        for (Section section : sections) {
            upStationToFindDown.put(section.getUpStation(), section.getDownStation());
            downStationToFindUp.put(section.getDownStation(), section.getUpStation());
        }
    }

    public Section modifyRelatedToAdd(Section newSection) {
        validateAddable(newSection);

        List<Section> related = findRelated(newSection);
        if (related.size() == IN_BETWEEN) {
            return modifyInBetweenCase(newSection, related);
        }

        if (related.size() == ONLY_END_RELATED) {
            final Section originalSection = related.get(FIRST_ELEMENT);
            return originalSection.updateByNewSection(newSection);
        }
        throw new InternalLogicConflictException();
    }

    public void add(Section newSection) {
        sections.add(newSection);
    }

    private List<Section> findRelated(Section newSection) {
        return sections.stream()
                .filter(section -> section.isAdjacent(newSection))
                .collect(Collectors.toList());
    }

    private Section modifyInBetweenCase(Section newSection, List<Section> related) {
        if (related.stream().anyMatch(section -> section.isUpStation(newSection.getUpStation()))) {
            Section sameHead = related.stream()
                    .filter(section -> section.isUpStation(newSection.getUpStation()))
                    .findAny().orElseThrow(InternalLogicConflictException::new);
            return sameHead.updateByNewSection(newSection);
        }
        Section sameTail = related.stream()
                .filter(section -> section.isDownStation(newSection.getDownStation()))
                .findAny().orElseThrow(InternalLogicConflictException::new);
        return sameTail.updateByNewSection(newSection);
    }

    private void validateAddable(Section target) {
        if (isDuplicatedSection(target)) {
            throw new SectionDuplicatedException();
        }

        if (isCycleSection(target, sections)) {
            throw new SectionCycleException();
        }

        if (isUnLinkableSection(target)) {
            throw new SectionUnlinkedException();
        }
    }

    private boolean isDuplicatedSection(Section target) {
        return sections.stream().anyMatch(section -> section.isSameOrReversed(target));
    }

    private boolean isCycleSection(Section newSection, List<Section> collect) {
        return collect.stream().anyMatch(section -> section.isUpStation(newSection.getUpStation())) &&
                collect.stream().anyMatch(section -> section.isDownStation(newSection.getDownStation()));
    }

    private boolean isUnLinkableSection(Section target) {
        return sections.stream().noneMatch(section -> section.isAdjacent(target));
    }

    public List<Section> sections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Section> removeRelated(Station station) {
        validateRemovable(station);

        List<Section> related = findRelated(station);
        sections = sections.stream()
                .filter(section -> !related.contains(section))
                .collect(Collectors.toList());

        return related;
    }

    public Section reflectRemoved(List<Section> related, Station station) {
        if (related.size() == IN_BETWEEN) {
            return mergeTwoIntoOne(related, station);
        }
        if (related.size() == ONLY_END_RELATED) {
            Section section = related.get(FIRST_ELEMENT);
            sections.add(section);
            return section;
        }
        throw new InternalLogicConflictException();
    }

    private void validateRemovable(Station station) {
        if (sections.stream().noneMatch(section -> section.isAdjacent(station))) {
            throw new StationNotFoundException();
        }

        if (sections.size() == LAST_ONE) {
            throw new SectionLastRemainedException();
        }
    }

    private List<Section> findRelated(Station station) {
        return sections.stream()
                .filter(section -> section.isAdjacent(station))
                .collect(Collectors.toList());
    }

    private Section mergeTwoIntoOne(List<Section> related, Station station) {
        Section firstRelated = related.get(FIRST_ELEMENT);
        Section secondRelated = related.get(SECOND_ELEMENT);
        int distance = firstRelated.getDistance() + secondRelated.getDistance();
        if (firstRelated.isUpStation(station)) {
            Station downStation = firstRelated.getDownStation();
            Station upStation = secondRelated.getUpStation();
            Section modified = Section.create(upStation, downStation, distance);
            sections.add(modified);
            return modified;
        }
        Station upStation = firstRelated.getUpStation();
        Station downStation = secondRelated.getDownStation();
        Section modified = Section.create(upStation, downStation, distance);
        sections.add(modified);
        return modified;
    }


    public boolean hasSize(int size) {
        return sections.size() == size;
    }

}
