package wooteco.subway.line.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import wooteco.subway.line.domain.Line;

public class LineServiceDto {

    private final Long id;
    @NotEmpty
    private final String name;
    @NotEmpty
    private final String color;

    public LineServiceDto(final Long id) {
        this(id, null, null);
    }

    public LineServiceDto(final String name, final String color) {
        this(null, name, color);
    }

    public LineServiceDto(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineServiceDto from(final Long id, final @Valid UpdateLineRequest lineRequest) {
        return new LineServiceDto(id, lineRequest.getName(), lineRequest.getColor());
    }

    public static LineServiceDto from(final LineRequest lineRequest) {
        return new LineServiceDto(lineRequest.getName(), lineRequest.getColor());
    }

    public static LineServiceDto from(final Line line) {
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