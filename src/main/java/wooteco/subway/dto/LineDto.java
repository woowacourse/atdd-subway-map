package wooteco.subway.dto;

public class LineDto {

    private Long id;
    private String name;
    private String color;

    public LineDto(Long id, String name, String color) {
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
