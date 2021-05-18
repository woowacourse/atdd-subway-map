package wooteco.subway.domain;

import wooteco.subway.exception.station.StationNotFoundException;

import java.util.*;

public class Stations {
    private static final int FIRST_ELEMENT = 0;

    private final List<Section> sections;

    public Stations(Sections sections) {
        this.sections = sections.getList();
    }

    public Stations() {
        this(new Sections());
    }

    private List<Station> convertToSortedStations() {
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

    public List<Station> getStations() {
        if (sections.size() == 0) {
            throw new StationNotFoundException();
        }
        return convertToSortedStations();
    }

}
