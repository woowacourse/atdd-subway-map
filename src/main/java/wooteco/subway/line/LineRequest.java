package wooteco.subway.line;

import wooteco.subway.line.section.Section;

public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private int extraFare;

    public LineRequest() {
    }

    public LineRequest(final String name, final String color) {
        this(name, color, null, null, 0, 0);
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
