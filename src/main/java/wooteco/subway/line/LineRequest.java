package wooteco.subway.line;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import wooteco.subway.line.section.Section;

public class LineRequest {

    @NotBlank(message = "이름이 입력되지 않았거나 공백입니다.")
    private String name;

    @NotBlank(message = "색깔이 입력되지 않았거나 공백입니다.")
    private String color;

    @NotNull(message = "상행역이 입력되지 않았습니다.")
    private Long upStationId;

    @NotNull(message = "하행역이 입력되지 않았습니다.")
    private Long downStationId;

    @Positive(message = "거리는 양수여야 합니다.")
    private int distance;

    @Positive(message = "추가요금은 양수여야 합니다.")
    private int extraFare;

    public LineRequest() {
    }

    public LineRequest(final String name, final String color, final Long upStationId, final Long downStationId,
        final int distance, final int extraFare) {

        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.extraFare = extraFare;
    }

    public Line toEntity() {
        return new Line(name, color);
    }

    public Section toSectionEntity(final Long lineId) {
        return Section.Builder()
            .lineId(lineId)
            .upStationId(upStationId)
            .downStationId(downStationId)
            .distance(distance)
            .build();
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

    public int getExtraFare() {
        return extraFare;
    }
}
