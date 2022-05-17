package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Sections {
    private static final int MIN_SIZE = 1;

    private List<Section> sections;

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of(List<Section> sections) {
        Map<Station, Station> stations = sections.stream()
            .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
        Station upStation = findUpStation(stations);
        List<Section> newSections = getSortedSections(sections, stations, upStation);

        return new Sections(newSections);
    }

    private static Station findUpStation(Map<Station, Station> stations) {
        return stations.keySet().stream()
            .filter(station -> !stations.containsValue(station))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당 구간을 찾을 수 없습니다."));
    }

    private static List<Section> getSortedSections(List<Section> sections, Map<Station, Station> stations,
        Station upStation) {
        List<Section> newSections = new LinkedList<>();
        while (stations.containsKey(upStation)) {
            final Station station = upStation;
            newSections.add(findSectionByUpStation(sections, station));
            upStation = stations.get(upStation);
        }
        return newSections;
    }

    private static Section findSectionByUpStation(List<Section> sections, Station station) {
        return sections.stream()
            .filter(section -> section.hasUpStation(station))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당 구간을 찾을 수 없습니다."));
    }

    public void insert(Section section) {
        checkContainsStation(section);

        LinkedList<Section> flexibleSections = new LinkedList<>(this.sections);
        OptionalInt findIndex = IntStream.range(0, flexibleSections.size())
            .filter(index -> canInsertSection(section, flexibleSections.get(index)))
            .findFirst();
        if (findIndex.isPresent()) {
            int index = findIndex.getAsInt();
            insertSection(section, flexibleSections, index, flexibleSections.get(index));
            return;
        }

        insertSectionSide(section, flexibleSections);
    }

    private boolean canInsertSection(Section section, Section sectionInLine) {
        return canInsertLeft(section, sectionInLine) || canInsertRight(section, sectionInLine);
    }

    private void insertSection(Section section, LinkedList<Section> flexibleSections,
        int index, Section sectionInLine) {

        if (canInsertLeft(section, sectionInLine)) {
            flexibleSections.add(index, section);
            sectionInLine.updateUpStation(section.getDownStation(),
                sectionInLine.getDistance() - section.getDistance());
        }
        if (canInsertRight(section, sectionInLine)) {
            flexibleSections.add(index + 1, section);
            sectionInLine.updateDownStation(section.getUpStation(),
                sectionInLine.getDistance() - section.getDistance());
        }
        sections = flexibleSections;
    }

    private void checkContainsStation(Section section) {
        List<Station> stations = getStations();
        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
            throw new IllegalArgumentException("이미 존재하는 상행선과 하행선은 구간에 추가할 수 없습니다.");
        }
    }

    private void insertSectionSide(Section section, LinkedList<Section> flexibleSections) {
        Section lastSection = sections.get(sections.size() - 1);
        if (lastSection.hasDownStation(section.getUpStation())) {
            flexibleSections.add(flexibleSections.size(), section);
            sections = flexibleSections;
            return;
        }

        Section firstSection = sections.get(0);
        if (firstSection.hasUpStation(section.getDownStation())) {
            flexibleSections.add(0, section);
            sections = flexibleSections;
            return;
        }

        throw new IllegalArgumentException("구간을 추가하지 못했습니다.");
    }

    private boolean canInsertLeft(Section section, Section sectionInLine) {
        return canInsertUpStation(section, sectionInLine) && !canInsertDownStation(section, sectionInLine);
    }

    private boolean canInsertRight(Section section, Section sectionInLine) {
        return canInsertDownStation(section, sectionInLine) && !canInsertUpStation(section, sectionInLine);
    }

    private boolean canInsertUpStation(Section section, Section sectionInLine) {
        return sectionInLine.hasUpStation(section.getUpStation())
            && sectionInLine.isLongerThan(section.getDistance());
    }

    private boolean canInsertDownStation(Section section, Section sectionInLine) {
        return sectionInLine.hasDownStation(section.getDownStation())
            && sectionInLine.isLongerThan(section.getDistance());
    }

    public Long delete(Station station) {
        LinkedList<Section> flexibleSections = new LinkedList<>(this.sections);
        validateMinSize(flexibleSections);

        int lastIndex = flexibleSections.size() - 1;
        if (isTopStation(station, flexibleSections)) {
            return removeSideStation(flexibleSections, 0);
        }

        if (isBottomStation(station, flexibleSections, lastIndex)) {
            return removeSideStation(flexibleSections, lastIndex);
        }

        return deleteMiddleSection(station, flexibleSections, lastIndex);
    }

    private void validateMinSize(LinkedList<Section> flexibleSections) {
        if (flexibleSections.size() == MIN_SIZE) {
            throw new IllegalArgumentException("한개 남은 구간은 제거할 수 없습니다.");
        }
    }

    private boolean isTopStation(Station station, LinkedList<Section> flexibleSections) {
        return flexibleSections.get(0).hasUpStation(station);
    }

    private Long removeSideStation(LinkedList<Section> flexibleSections, int index) {
        Section section = flexibleSections.remove(index);
        sections = flexibleSections;
        return section.getId();
    }

    private boolean isBottomStation(Station station, LinkedList<Section> flexibleSections, int lastIndex) {
        return flexibleSections.get(lastIndex).hasDownStation(station);
    }

    private Long deleteMiddleSection(Station station,
        LinkedList<Section> flexibleSections, int lastIndex) {
        OptionalInt index = IntStream.range(0, lastIndex)
            .filter(i -> sections.get(i).hasDownStation(station))
            .findFirst();
        if (index.isPresent()) {
            int indexAsInt = index.getAsInt();
            return removeMiddleStation(flexibleSections, indexAsInt, sections.get(indexAsInt));
        }
        return -1L;
    }

    private Long removeMiddleStation(LinkedList<Section> flexibleSections, int index, Section leftSection) {
        Section rightSection = sections.get(index + 1);
        leftSection.updateDownStation(rightSection.getDownStation(),
            leftSection.getDistance() + rightSection.getDistance());
        flexibleSections.remove(rightSection);
        sections = flexibleSections;
        return rightSection.getId();
    }

    public List<Section> getSections() {
        return new LinkedList<>(sections);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations.stream()
            .distinct()
            .collect(Collectors.toList());
    }
}
