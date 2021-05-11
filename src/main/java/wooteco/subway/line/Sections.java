package wooteco.subway.line;

import wooteco.subway.line.exception.SectionError;
import wooteco.subway.line.exception.SectionException;
import wooteco.subway.station.Station;

import java.util.*;

public class Sections {
    private final List<Section> sections;
    private final Deque<Station> path;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
        this.path = buildPath(sections);
    }

    private Deque<Station> buildPath(List<Section> sections) {
        Map<Station, Section> upStationMap = new HashMap<>();
        Map<Station, Section> downStationMap = new HashMap<>();

        Deque<Station> sortedStation = new ArrayDeque<>();

        for (Section section : sections) {
            upStationMap.put(section.getUpStation(), section);
            downStationMap.put(section.getDownStation(), section);
        }

        sortedStation.addFirst(sections.get(0)
                                       .getUpStation());
        sortedStation.addLast(sections.get(0)
                                      .getDownStation());

        while (upStationMap.containsKey(sortedStation.getLast())) {
            sortedStation.addLast(upStationMap.get(sortedStation.getLast())
                                              .getDownStation());
        }

        while (downStationMap.containsKey(sortedStation.getFirst())) {
            sortedStation.addFirst(downStationMap.get(sortedStation.getFirst())
                                                 .getUpStation());
        }

        return sortedStation;
    }

    public Sections add(Section section) {
        checkSameStationInput(section);
        checkBothStationInPath(section);
        checkBothStationNotInPath(section);
        if (isEndPointAddition(section)) {
            sections.add(section);
        }

        return new Sections(sections);
    }

    private void checkSameStationInput(Section section) {
        if (section.getDownStation()
                   .equals(section.getUpStation())) {
            throw new SectionException(SectionError.SAME_STATION_INPUT);
        }
    }

    private void checkBothStationInPath(Section section) {
        if (path.contains(section.getDownStation()) && path.contains(section.getUpStation())) {
            throw new SectionException(SectionError.BOTH_STATION_IN_PATH);
        }
    }

    private void checkBothStationNotInPath(Section section) {
        if (!path.contains(section.getDownStation()) && !path.contains(section.getUpStation())) {
            throw new SectionException(SectionError.NONE_STATION_IN_PATH);
        }
    }

    private boolean isEndPointAddition(Section section) {
        Station firstStation = path.getFirst();
        Station lastStation = path.getLast();
        return firstStation.equals(section.getDownStation()) || lastStation.equals(section.getUpStation());
    }

    public List<Station> path() {
        return new ArrayList<>(path);
    }
}
