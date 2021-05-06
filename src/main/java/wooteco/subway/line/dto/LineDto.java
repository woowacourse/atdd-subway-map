package wooteco.subway.line.dto;

public class LineDto {

    private final Long id;
    private final String name;
    private final String color;

    public LineDto(final Long id) {
        this(id, null, null);
    }

    public LineDto(final String name, final String color) {
        this(null, name, color);
    }

    public LineDto(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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
