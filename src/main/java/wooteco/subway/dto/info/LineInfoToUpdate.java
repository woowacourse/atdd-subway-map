package wooteco.subway.dto.info;

public class LineInfoToUpdate {
    private Long id;
    private String name;
    private String color;

    public LineInfoToUpdate(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineInfoToUpdate(Long id, String name, String color) {
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
