package wooteco.subway.section.model;

import wooteco.subway.station.model.Station;

import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> sortedStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(sections.get(stations.size() - 1).getDownStation());
        return stations;
    }
}
