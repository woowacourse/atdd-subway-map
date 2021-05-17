package wooteco.subway.line.dto.response;

import wooteco.subway.line.Line;
import wooteco.subway.section.Section;
import wooteco.subway.station.dto.StationResponse;

public class LineCreateResponse {
    private long id;
    private String name;
    private String color;
    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;

    public LineCreateResponse() {
    }

    public LineCreateResponse(Line line, Section section) {
        this(line.getId(), line.getName(), line.getColor(), new StationResponse(section.getUpStation()),
                new StationResponse(section.getDownStation()), section.getDistance());
    }

    public LineCreateResponse(long id, String name, String color, StationResponse upStation,
                              StationResponse downStation, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public StationResponse getUpStation() {
        return upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
