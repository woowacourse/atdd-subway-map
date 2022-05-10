package wooteco.subway.dto;

import java.util.List;
import wooteco.subway.dao.entity.LineEntity;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(LineEntity lineEntity, List<StationResponse> stations) {
        this.id = lineEntity.getId();
        this.name = lineEntity.getName();
        this.color = lineEntity.getColor();
        this.stations = stations;
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
