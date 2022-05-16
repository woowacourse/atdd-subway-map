package wooteco.subway.dto;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class LineRequest {

    @NotBlank(message = "노선 이름은 공백일 수 없습니다.")
    private String name;

    @NotNull(message = "노선 색상은 공백일 수 없습니다.")
    private String color;

    @NotNull(message = "노선 구간의 상행 아이디는 공백일 수 없습니다.")
    private Long upStationId;

    @NotNull(message = "노선 구간의 하행 아이디는 공백일 수 없습니다.")
    private Long downStationId;

    @Positive
    @NotNull(message = "구간의 거리는 공백이거나 음수일 수 없습니다.")
    private int distance;


    private LineRequest() {
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
        return new Section(new Station(upStationId, ""), new Station(downStationId, ""), distance);
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
