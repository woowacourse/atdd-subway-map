package wooteco.subway.line;

import wooteco.subway.section.Section;
import wooteco.subway.station.Station;

import java.util.Collections;
import java.util.List;

public class Line {

    private Long id;
    private String name;
    private String color;
    private List<Station> stations;
    private List<Section> sections;

    public Line() {
    }

    public Line(Long id) {
        this(id, null, null, Collections.emptyList());
    }

    public Line(String name, String color) {
        this(null, name, color, Collections.emptyList(), Collections.emptyList());
    }

    public Line(Long id, String name, String color, List<Station> stations) {
        this(id, name, color, stations, Collections.emptyList());
    }

    public Line(Long id, String name, String color, List<Station> stations, List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.sections = sections;
    }

    public Line update(String lineName, String lineColor) {
        return new Line(id, lineName, lineColor, stations);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return stations;
    }
}
