package wooteco.subway.section.domain;

import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.Stations;
import wooteco.subway.station.service.NoSuchStationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(final Section section) {
        sections.add(section);
    }

    public Stations getOrderedStations() {
        Map<Station, Station> upAndDownStations = toSectionMap();

        Station firstStation = getFirstStation(upAndDownStations);
        List<Station> stations = new ArrayList<>(Collections.singletonList(firstStation));

        for (int i = 0; i < upAndDownStations.size(); i++) {
            Station currentDownEndStation = stations.get(stations.size() - 1);
            Station nextDownStation = upAndDownStations.get(currentDownEndStation);
            stations.add(nextDownStation);
        }
        return new Stations(stations);
    }

    private Map<Station, Station> toSectionMap() {
        return sections.stream().collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
    }

    private Station getFirstStation(final Map<Station, Station> upAndDownStations) {
        return upAndDownStations.keySet()
                .stream()
                .filter(station -> !upAndDownStations.containsValue(station))
                .findFirst()
                .orElseThrow(NoSuchStationException::new);
    }
}
