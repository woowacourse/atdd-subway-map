package wooteco.subway.line.api.dto;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.line.model.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(Line newLine) {
        this.id = newLine.getId();
        this.name = newLine.getName();
        this.color = newLine.getColor();
    }

    public LineResponse(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static List<LineResponse> listOf(List<Line> lines) {
        return lines.stream()
            .map(LineResponse::new)
            .collect(Collectors.toList());
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
