package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> getOrderedStations() {
        List<Station> stations = new LinkedList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations;
    }
}
