package wooteco.subway.domain;


import javax.validation.constraints.NotBlank;

public class Line {
    private final Long id;
    @NotBlank(message = "노선 이름은 필수로 입력하여야 합니다.")
    private final String name;
    @NotBlank(message = "노선 색상은 필수로 입력하여야 합니다.")
    private final String color;

    private Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static Line of(Long id, String name, String color) {
        return new Line(id, name, color);
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

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }
}
