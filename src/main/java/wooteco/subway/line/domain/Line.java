package wooteco.subway.line.domain;

import wooteco.subway.line.dto.LineCreateRequest;
import wooteco.subway.line.dto.LineUpdateRequest;

public class Line {

    private Long id;
    private String name;
    private String color;

    private Line() {
    }

    private Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }
    public static Line of(Long id, String name, String color) {
        return new Line(id, name, color);
    }

    public static Line of(LineCreateRequest lineCreateRequest) {
        return new Line(null, lineCreateRequest.getName(), lineCreateRequest.getColor());
    }

    public static Line of(Long id, LineUpdateRequest lineUpdateRequest) {
        return new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor());
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
}
