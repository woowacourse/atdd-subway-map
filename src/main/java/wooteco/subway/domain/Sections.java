package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = List.copyOf(sections);
    }

    public List<Station> getSortedStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Station, Station> goesDownStations = new HashMap<>();
        Map<Station, Station> goesUpStations = new HashMap<>();
        fillStations(goesDownStations, goesUpStations);

        return Collections.unmodifiableList(sortStations(goesDownStations, goesUpStations));
    }

    private LinkedList<Station> sortStations(Map<Station, Station> goesDownStations,
                                             Map<Station, Station> goesUpStations) {
        LinkedList<Station> stations = new LinkedList<>();
        Station station = sections.get(0).getUpStation();
        stations.add(station);

        addGoesDownStations(goesDownStations, stations, station);
        addGoesUpStations(goesUpStations, stations, station);
        return stations;
    }

    private void addGoesDownStations(Map<Station, Station> goesDownStations, LinkedList<Station> stations,
                                     Station station) {
        Station tempStation = station;
        while (goesDownStations.containsKey(tempStation)) {
            tempStation = goesDownStations.get(tempStation);
            stations.add(tempStation);
        }
    }

    private void addGoesUpStations(Map<Station, Station> goesUpStations, LinkedList<Station> stations,
                                   Station station) {
        Station tempStation;
        tempStation = station;
        while (goesUpStations.containsKey(tempStation)) {
            tempStation = goesUpStations.get(tempStation);
            stations.addFirst(tempStation);
        }
    }

    private void fillStations(Map<Station, Station> goesDownStations, Map<Station, Station> goesUpStations) {
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            goesDownStations.put(upStation, downStation);
            goesUpStations.put(downStation, upStation);
        }
    }

}
