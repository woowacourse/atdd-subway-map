package wooteco.subway.section;

import wooteco.subway.station.Station;
import wooteco.subway.station.StationResponse;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<StationResponse> sortedStations() {
        Deque<Station> sortedStations = new ArrayDeque<>();
        Map<Station, Station> upStations = new LinkedHashMap<>();
        Map<Station, Station> downStations = new LinkedHashMap<>();

        initializeByStations(sortedStations, upStations, downStations);
        sortByStations(sortedStations, upStations, downStations);

        List<Station> stations = new ArrayList<>(sortedStations);
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private void initializeByStations(Deque<Station> sortedStations, Map<Station, Station> upStations,
                                      Map<Station, Station> downStations) {
        for (Section section : sections) {
            upStations.put(section.getUpStation(), section.getDownStation());
            downStations.put(section.getDownStation(), section.getUpStation());
        }

        Section curSection = sections.get(0);
        sortedStations.addFirst(curSection.getUpStation());
        sortedStations.addLast(curSection.getDownStation());
    }

    private void sortByStations(Deque<Station> sortedStations, Map<Station, Station> upStations,
                                Map<Station, Station> downStations) {
        while (downStations.containsKey(sortedStations.peekFirst())) {
            Station station = sortedStations.peekFirst();
            sortedStations.addFirst(downStations.get(station));
        }
        while (upStations.containsKey(sortedStations.peekLast())) {
            Station station = sortedStations.peekLast();
            sortedStations.addLast(upStations.get(station));
        }
    }
}
