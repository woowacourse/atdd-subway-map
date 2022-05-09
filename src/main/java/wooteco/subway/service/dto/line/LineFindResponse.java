package wooteco.subway.service.dto.line;

public class LineFindResponse {

    private final Long id;
    private final String name;
    private final String color;

    public LineFindResponse(Long id, String name, String color) {
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
