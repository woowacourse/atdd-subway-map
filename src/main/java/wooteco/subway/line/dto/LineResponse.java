package wooteco.subway.line.dto;

import java.util.List;
import wooteco.subway.line.Line;
import wooteco.subway.station.dto.StationResponse;

public class LineResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;
    private final List<StationResponse> stations;

    public LineResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor(), line.getUpStationId(),
            line.getDownStationId(), line.getDistance(), null);
    }

    public LineResponse(Long id, String name, String color, Long upStationId,
        Long downStationId, int distance) {
        this(id, name, color, upStationId, downStationId, distance, null);
    }

    public LineResponse(Long id, String name, String color, Long upStationId,
        Long downStationId, int distance, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
