package wooteco.subway.line;

import wooteco.subway.station.Station;

import java.util.*;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public List<Station> path() {
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

        return new ArrayList<>(sortedStation);
    }

    public void add(Section section) {
        sections.add(section);
    }
}
