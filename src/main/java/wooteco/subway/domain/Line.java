package wooteco.subway.domain;

import java.util.ArrayList;
import wooteco.subway.dao.entity.StationEntity;

public class Line {

    private final String name;
    private final String color;
    private final ArrayList<StationEntity> stations;

    public Line(String name, String color, ArrayList<StationEntity> stations) {
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Line(String name, String color) {
        this(name, color, null);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public ArrayList<StationEntity> getStations() {
        return stations;
    }
}
