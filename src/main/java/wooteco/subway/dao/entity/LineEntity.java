package wooteco.subway.dao.entity;

public class LineEntity {

    private Long id;
    private String name;
    private String color;

    public LineEntity() {
    }

    public LineEntity(Long id, String name, String color) {
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
