package wooteco.subway.reopository.entity;

public class LineEntity {

    private Long id;
    private String name;
    private String color;

    public LineEntity(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineEntity(String name, String color) {
        this(null, name, color);
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
