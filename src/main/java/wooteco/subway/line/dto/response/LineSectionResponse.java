package wooteco.subway.line.dto.response;

import wooteco.subway.section.dto.response.SectionCreateResponse;

public class LineSectionResponse {
    private long id;
    private String name;
    private String color;
    private long upStationId;
    private long downStationId;
    private int distance;

    public LineSectionResponse() {
    }

    public LineSectionResponse(LineCreateResponse line, SectionCreateResponse section) {
        this(line.getId(), line.getName(), line.getColor(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public LineSectionResponse(long id, String name, String color, long upStationId, long downStationId, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
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

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
