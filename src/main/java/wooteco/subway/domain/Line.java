package wooteco.subway.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class Line {
    private Long id;
    private String name;
    private String color;
    private Stations stations;

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, new Stations());
    }

    public Line(Long id, String name, String color, Stations stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public List<Station> getStations() {
        return stations.getStations();
    }

    public boolean isSameColor(String color) {
        return this.color.equals(color);
    }

    public boolean isSameId(Long lineId) {
        return id.equals(lineId);
    }

    public void setStationsBySections(Sections sections) {
        this.stations = new Stations(sections);
    }
}
