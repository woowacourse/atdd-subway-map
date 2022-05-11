package wooteco.subway.dto.response;

import java.util.ArrayList;
import java.util.List;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse() {
    }

    public LineResponse(Long id, String name, String color) {
        this(id, name, color, null);
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line savedLine, Station upStation, Station downStation) {
        List<StationResponse> stations = new ArrayList<>();
        stations.add(StationResponse.of(upStation));
        stations.add(StationResponse.of(downStation));

        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor(), stations);
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

    public List<StationResponse> getStations() {
        return stations;
    }
}
