package wooteco.subway.dto.request;

import java.util.Objects;
import wooteco.subway.domain.Line;

public class LineRequest {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;


    private LineRequest() {
    }

    public LineRequest(final String name,
                       final String color,
                       final Long upStationId,
                       final Long downStationId,
                       final int distance) {
        Objects.requireNonNull(name, ERROR_NULL);
        Objects.requireNonNull(color, ERROR_NULL);
        Objects.requireNonNull(upStationId, ERROR_NULL);
        Objects.requireNonNull(downStationId, ERROR_NULL);
        Objects.requireNonNull(distance, ERROR_NULL);
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Line toEntity(final Long id) {
//        return new Line(id, this.name, this.color, this.upStationId, this.downStationId, this.distance);
        return new Line(id, this.name, this.color);
    }

    public Line toEntity() {
        return toEntity(null);
    }
}
