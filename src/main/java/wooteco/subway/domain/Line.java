package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Line {
    private Long id;
    private String name;
    private String color;
    private Stations stations;

    public static Line create(String name, String color) {
        return create(null, name, color);
    }

    public static Line create(Long id, String name, String color) {
        return create(id, name, color, Stations.create());
    }

    public static Line create(Long id, String name, String color, Stations stations) {
        return new Line(id, name, color, stations);
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
        this.stations = Stations.create(sections);
    }
}
