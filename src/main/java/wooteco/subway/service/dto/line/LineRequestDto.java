package wooteco.subway.service.dto.line;

public class LineRequestDto {

    public static final long DEFAULT_VALUE = 0L;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequestDto() {
    }

    public LineRequestDto(String name, String color) {
        this(name, color, DEFAULT_VALUE, DEFAULT_VALUE, 0);
    }

    public LineRequestDto(String name, String color, Long upStationId, Long downStationId, int distance) {
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
}
