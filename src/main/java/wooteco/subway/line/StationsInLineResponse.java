package wooteco.subway.line;

import java.util.List;
import wooteco.subway.station.Station;

public class StationsInLineResponse {

    private long id;
    private String name;
    private String color;
    private List<Station> stations;

    public StationsInLineResponse(Line line, List<Station> stationsInSection) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stationsInSection;
    }

    public long getId() {
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
