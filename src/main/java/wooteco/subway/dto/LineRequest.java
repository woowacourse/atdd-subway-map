package wooteco.subway.dto;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import javax.validation.constraints.NotBlank;

public class LineRequest {

    @NotBlank(message = "노선 이름은 공백일 수 없습니다.")
    private final String name;

    @NotBlank(message = "노선 색상은 공백일 수 없습니다.")
    private final String color;

    private final Long upStationId;

    private final Long downStationId;

    private final int distance;

    public LineRequest() {
        this(null, null, null, null, 0);
    }

    public LineRequest(final String name, final String color) {
        this(name, color, null, null, 0);
    }

    public LineRequest(final String name,
                       final String color,
                       final Long upStationId,
                       final Long downStationId,
                       final int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toEntity() {
        return new Line(name, color);
    }

    public Section toSectionEntity() {
        validateUpStation();
        validateDownStation();
        validateDistance();
        return new Section(new Station(upStationId, ""), new Station(downStationId, ""), distance);
    }

    private void validateUpStation() {
        if (upStationId == null) {
            throw new IllegalArgumentException("상행역은 비어있을 수 없습니다.");
        }
    }

    private void validateDownStation() {
        if (downStationId == null) {
            throw new IllegalArgumentException("하행역은 비어있을 수 없습니다.");
        }
    }

    private void validateDistance() {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 양수이어야 합니다.");
        }
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
}
