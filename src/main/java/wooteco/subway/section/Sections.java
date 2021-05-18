package wooteco.subway.section;

import wooteco.subway.section.exception.SectionError;
import wooteco.subway.section.exception.SectionException;
import wooteco.subway.station.Station;

import java.util.*;

public class Sections {
    private static final int SECTIONS_MIN_SIZE = 2;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public List<Station> path() {
        Map<Station, Section> sectionsFromUpStation = sectionsFromUpStation();
        Station station = upStation();

        List<Station> path = new ArrayList<>();
        path.add(station);

        while (hasNext(station, sectionsFromUpStation)) {
            station = nextStation(station, sectionsFromUpStation);
            path.add(station);
        }
        return path;
    }

    private boolean hasNext(Station station, Map<Station, Section> sectionsFromUpStation) {
        return sectionsFromUpStation.containsKey(station);
    }

    private Station nextStation(Station station, Map<Station, Section> sectionsFromUpStation) {
        return sectionsFromUpStation.get(station)
                                    .getDownStation();
    }

    private Station upStation() {
        return findDifferentStation(upStations(), downStations());
    }

    private Station downStation() {
        return findDifferentStation(downStations(), upStations());
    }

    private Station findDifferentStation(Set<Station> from, Set<Station> to) {
        return from.stream()
                   .filter(station -> !to.contains(station))
                   .findAny()
                   .orElseThrow(() -> new SectionException(SectionError.CANNOT_FIND_DIFFERENT_STATION));
    }

    private Map<Station, Section> sectionsFromUpStation() {
        Map<Station, Section> map = new HashMap<>();
        for (Section section : sections) {
            map.put(section.getUpStation(), section);
        }
        return map;
    }

    private Map<Station, Section> sectionsFromDownStation() {
        Map<Station, Section> map = new HashMap<>();
        for (Section section : sections) {
            map.put(section.getDownStation(), section);
        }
        return map;
    }

    private Set<Station> upStations() {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
        }
        return stations;
    }

    private Set<Station> downStations() {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return stations;
    }

    public void add(Section section) {
        List<Station> path = path();
        checkBothStationInPath(section, path);
        checkBothStationNotInPath(section, path);

        if (isEndPoint(section)) {
            sections.add(section);
            return;
        }

        if (isMiddleUpToDown(section)) {
            splitSectionFromUpToDown(section);
            return;
        }

        if (isMiddleDownToUp(section)) {
            splitSectionFromDownToUp(section);
            return;
        }
        throw new SectionException(SectionError.UNMATCHED_ADD_ERROR);
    }

    private void splitSectionFromUpToDown(Section section) {
        Section origin = sectionsFromUpStation().get(section.getUpStation());
        Section newSection = new Section(section.getDownStation(), origin.getDownStation(), replacedDistance(section, origin));
        replaceSection(origin, newSection);
        sections.add(section);
    }

    private void splitSectionFromDownToUp(Section section) {
        Section origin = sectionsFromDownStation().get(section.getDownStation());
        Section newSection = new Section(origin.getUpStation(), section.getUpStation(), replacedDistance(section, origin));
        replaceSection(origin, newSection);
        sections.add(section);
    }

    private int replacedDistance(Section section, Section origin) {
        int replacedDistance = origin.getDistance() - section.getDistance();
        if (replacedDistance <= 0) {
            throw new SectionException(SectionError.CANNOT_DIVIDE_ORIGIN_SECTION);
        }
        return replacedDistance;
    }

    private void replaceSection(Section from, Section to) {
        sections.set(sections.indexOf(from), to);
    }

    private boolean isMiddleUpToDown(Section section) {
        return upStations().contains(section.getUpStation()) && !downStations().contains(section.getDownStation());
    }

    private boolean isMiddleDownToUp(Section section) {
        return !upStations().contains(section.getUpStation()) && downStations().contains(section.getDownStation());
    }

    private void checkBothStationInPath(Section section, List<Station> path) {
        if (path.contains(section.getDownStation()) && path.contains(section.getUpStation())) {
            throw new SectionException(SectionError.BOTH_STATION_IN_PATH);
        }
    }

    private void checkBothStationNotInPath(Section section, List<Station> path) {
        if (!path.contains(section.getDownStation()) && !path.contains(section.getUpStation())) {
            throw new SectionException(SectionError.NONE_STATION_IN_PATH);
        }
    }

    private boolean isEndPoint(Section section) {
        return upStation().equals(section.getDownStation()) || downStation().equals(section.getUpStation());
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }

    public void delete(Station station) {
        validateSectionsSize();

        Section upper = sectionsFromDownStation().get(station);
        Section lower = sectionsFromUpStation().get(station);

        if (upper == null && lower == null) {
            throw new SectionException(SectionError.NO_STATION_TO_DELETE);
        }

        if (upper == null) {
            sections.remove(lower);
            return;
        }

        if (lower == null) {
            sections.remove(upper);
            return;
        }

        merge(upper, lower);
    }

    private void validateSectionsSize() {
        if (sections.size() < SECTIONS_MIN_SIZE) {
            throw new SectionException(SectionError.CANNOT_DELETE_SECTION_SIZE_LESS_THAN_TWO);
        }
    }

    private void merge(Section upper, Section lower) {
        sections.remove(lower);
        sections.remove(upper);
        sections.add(new Section(upper.getUpStation(), lower.getDownStation(), upper.getDistance() + lower.getDistance()));
    }
}
