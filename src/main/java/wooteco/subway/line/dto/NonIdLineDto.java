package wooteco.subway.line.dto;

public class NonIdLineDto {

    private final String name;
    private final String color;

    public NonIdLineDto(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
