package wooteco.subway.service.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.request.UpdateLineRequest;
import wooteco.subway.domain.Line;

public class LineServiceDto {

    private final Long id;
    @NotEmpty
    private final String name;
    @NotBlank
    private final String color;

    public LineServiceDto(Long id) {
        this(id, null, null);
    }

    public LineServiceDto(String name, String color) {
        this(null, name, color);
    }

    public LineServiceDto(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineServiceDto from(Long id, @Valid UpdateLineRequest lineRequest) {
        return new LineServiceDto(id, lineRequest.getName(), lineRequest.getColor());
    }

    public static LineServiceDto from(LineRequest lineRequest) {
        return new LineServiceDto(lineRequest.getName(), lineRequest.getColor());
    }

    public static LineServiceDto from(Line line) {
        return new LineServiceDto(line.getId(), line.getName(), line.getColor());
    }

    public Line toEntity() {
        return new Line(name, color);
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
