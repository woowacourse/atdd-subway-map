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
    private static final int SECOND_ELEMENT = 1;
    private static final int EXPECTED_REMOVE_SITUATION = 2;
    private static final int TWO_ADJACENT_SECTIONS = 2;

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
            System.out.println(section);
            upStationToFindDown.put(section.getUpStation(), section.getDownStation());
            downStationToFindUp.put(section.getDownStation(), section.getUpStation());
        }
    }

    private boolean isCycleSection(Section newSection, List<Section> collect) {
        return collect.stream().anyMatch(section -> section.isUpStation(newSection.getUpStation())) &&
                collect.stream().anyMatch(section -> section.isDownStation(newSection.getDownStation()));
    }

//    private Section updateSection(Section originalSection, Section newSection) {
//        return
//    }

    public Section modifyRelated(Section newSection) {
        validateAddable(newSection);
        List<Section> related = findRelated(newSection);

        // 잠실 - 잠실새내 - 동탄  <-- 수서 - 잠실새내
        if (related.size() == 2) {
            if (related.stream().anyMatch(section -> section.isUpStation(newSection.getUpStation()))) {
                Section sameHead = related.stream()
                        .filter(section -> section.isUpStation(newSection.getUpStation()))
                        .findAny().orElseThrow(InternalLogicConflictException::new);
                return sameHead.updateByNewSection(newSection);
            }
            // 베이스가 tail일 경우
            if (related.stream().anyMatch(section -> section.isDownStation(newSection.getDownStation()))) {
                Section sameTail = related.stream()
                        .filter(section -> section.isDownStation(newSection.getDownStation()))
                        .findAny().orElseThrow(InternalLogicConflictException::new);
                return sameTail.updateByNewSection(newSection);
            }
        }

        if (related.size() == 1) {
            final Section originalSection = related.get(FIRST_ELEMENT);
            return originalSection.updateByNewSection(newSection);
        }
        throw new InternalLogicConflictException();
    }

    private List<Section> findRelated(Section newSection) {
        return sections.stream()
                .filter(section -> section.isAdjacent(newSection))
                .collect(Collectors.toList());
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

    private boolean isUnLinkableSection(Section target) {
        return sections.stream().noneMatch(section -> section.isAdjacent(target));
    }

    private boolean isDuplicatedSection(Section target) {
        return sections.stream().anyMatch(section -> section.isSameOrReversed(target));
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
        if (related.size() == 2) {
            Section firstRelated = related.get(0);
            Section secondRelated = related.get(1);
            int distance = firstRelated.getDistance() + secondRelated.getDistance();
            if (firstRelated.isUpStation(station)) {
                Station downStation = firstRelated.getDownStation();
                Station upStation = secondRelated.getUpStation();
                Section modified = Section.create(upStation, downStation, distance);
                sections.add(modified);
                return modified;
            }
        }
        if (related.size() == 1) {
            Section section = related.get(0);
            sections.add(section);
            return section;
        }
        throw new InternalLogicConflictException();
    }

    private List<Section> findRelated(Station station) {
        return sections.stream()
                .filter(section -> section.isAdjacent(station))
                .collect(Collectors.toList());
    }

    private void validateRemovable(Station station) {
        if (sections.stream().noneMatch(section -> section.isAdjacent(station))) {
            throw new StationNotFoundException();
        }

        if (sections.size() == 1) {
            throw new SectionLastRemainedException();
        }
    }

    public boolean hasSize(int size) {
        return sections.size() == size;
    }


    public void add(Section newSection) {
        sections.add(newSection);
    }

}
