package wooteco.subway.dto;

import java.util.ArrayList;
import java.util.List;
import wooteco.subway.entity.LineEntity;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = new ArrayList<>(stations);
    }

    public LineResponse(LineEntity lineEntity, List<StationResponse> stations) {
        this(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), stations);
    }

    public LineResponse(LineEntity lineEntity) {
        this(lineEntity.getId(), lineEntity.getName(), lineEntity.getColor(), new ArrayList<>());
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
