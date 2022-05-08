package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line(Long id, String name, String color, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line(String name, String color, Long upStationId, Long downStationId, int distance) {
        this(0L, name, color, upStationId, downStationId, distance);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return distance == line.distance && Objects.equals(id, line.id) && Objects.equals(name,
                line.name) && Objects.equals(color, line.color) && Objects.equals(upStationId,
                line.upStationId) && Objects.equals(downStationId, line.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, upStationId, downStationId, distance);
    }
}
