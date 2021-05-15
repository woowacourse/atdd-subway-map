package wooteco.subway.line.dto.response;

import wooteco.subway.line.Line;

public class LineResponse {
    private long id;
    private String name;
    private String color;

    public LineResponse() {
    }

    public LineResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor());
    }

    public LineResponse(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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
}
