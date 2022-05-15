package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionType;
import wooteco.subway.exception.NotFoundException;

public class Sections2 {

    private static final String SECTIONS_NOT_CONNECTED_EXCEPTION = "구간들이 서로 이어지지 않습니다.";

    private final List<Section> value;

    public Sections2(List<Section> value) {
        validateLineExistence(value);
        List<Section> sortedSections = toSortedSections(value);
        validateConnection(value, sortedSections);
        this.value = Collections.unmodifiableList(sortedSections);
    }

    private static void validateLineExistence(List<Section> value) {
        if (value.isEmpty()) {
            throw new NotFoundException(ExceptionType.LINE_NOT_FOUND);
        }
    }

    private static List<Section> toSortedSections(List<Section> sections) {
        List<Section> list = new ArrayList<>();
        Map<Station, Section> sectionMap = toSectionMap(sections);
        Station current = toUpperEndStation(sections);

        while (sectionMap.containsKey(current)) {
            Section section = sectionMap.get(current);
            list.add(section);
            current = section.getDownStation();
        }
        return list;
    }

    private static Station toUpperEndStation(List<Section> sections) {
        Set<Station> downStations = toDownStations(sections);
        return sections.stream()
                .map(Section::getUpStation)
                .filter(it -> !downStations.contains(it))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상행 종점을 조회하는 데 실패하였습니다."));
    }

    private static Set<Station> toDownStations(List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toSet());
    }

    private static Map<Station, Section> toSectionMap(List<Section> sections) {
        Map<Station, Section> sectionMap = new HashMap<>();
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            sectionMap.put(upStation, section);
        }
        return sectionMap;
    }

    private static void validateConnection(List<Section> sections, List<Section> sortedSections) {
        if (sections.size() != sortedSections.size()) {
            throw new IllegalArgumentException(SECTIONS_NOT_CONNECTED_EXCEPTION);
        }
    }

    public boolean hasSingleSection() {
        return value.size() == 1;
    }

    public Station getUpperEndStation() {
        return getUpperEndSection().getUpStation();
    }

    public Section getUpperEndSection() {
        return value.get(0);
    }

    public Section getLowerEndSection() {
        return value.get(value.size() - 1);
    }

    public Section findUpperSectionOfStation(Station station) {
        return value.stream()
                .filter(section -> section.hasDownStationOf(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 지하철역의 상행 구간을 조회하는 데 실패하였습니다."));
    }

    public Section findLowerSectionOfStation(Station station) {
        return value.stream()
                .filter(section -> section.hasUpStationOf(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 지하철역의 하행 구간을 조회하는 데 실패하였습니다."));
    }

    public boolean isRegistered(Station station) {
        return value.stream()
                .anyMatch(section -> section.hasStationOf(station));
    }

    public boolean isRegisteredAsUpStation(Section section) {
        return isRegistered(section.getUpStation());
    }

    public boolean isRegisteredAsDownStation(Section section) {
        return isRegistered(section.getDownStation());
    }

    public boolean checkMiddleStation(Station station) {
        long registeredSectionCount = value.stream()
                .filter(section -> section.hasStationOf(station))
                .count();
        return registeredSectionCount == 2;
    }

    public List<Section> toSortedList() {
        return new ArrayList<>(value);
    }

    public List<Station> toSortedStations() {
        return new ArrayList<>() {{
            add(getUpperEndStation());
            addAll(toDownStations());
        }};
    }

    private List<Station> toDownStations() {
        return value.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections2 sections = (Sections2) o;
        return Objects.equals(value, sections.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Sections{" + "value=" + value + '}';
    }
}
