package wooteco.subway.dto;

import wooteco.subway.domain.line.Line;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LineRequest {
    @NotBlank(message = "유효하지 않은 노선 이름입니다.")
    private String name;

    @NotBlank(message = "유효하지 않은 노선 색상입니다.")
    private String color;

    @NotNull(message = "노선의 상행 종점 역을 입력해주세요.")
    private Long upStationId;

    @NotNull(message = "노선의 하행 종점 역을 입력해주세요")
    private Long downStationId;

    @NotNull
    @Min(value = 1, message = "거리는 1 이상이어야 합니다")
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line createLine() {
        return new Line(this.name, this.color);
    }

    public SectionRequest createSectionRequest() {
        return new SectionRequest(this.upStationId, this.downStationId, this.distance);
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
