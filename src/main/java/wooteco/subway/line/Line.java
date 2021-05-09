package wooteco.subway.line;

import wooteco.subway.station.Station;

import java.util.List;

public class Line {

    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public Line() {
    }

    public Line(String name, String color, List<Station> stations) {
        this(null, name, color, stations);
    }

    private Line(Long id, String name, String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static Line of(long lineId, String lineName, String lineColor) {
        return new Line(lineId, lineName, lineColor, null);
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
