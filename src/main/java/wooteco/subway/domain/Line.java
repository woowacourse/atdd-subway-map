package wooteco.subway.domain;

public class Line {

    private static final int NAME_LENGTH = 255;
    private static final int COLOR_LENGTH = 20;

    private Long id;
    private String name;
    private String color;

    public Line() {
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        validateName(name);
        validateColor(color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("이름은 비어있을 수 없습니다.");
        }
        if (name.length() > NAME_LENGTH) {
            throw new IllegalArgumentException("이름은 " + NAME_LENGTH + "자를 초과할 수 없습니다.");
        }
    }

    private void validateColor(String color) {
        if (color == null || color.isEmpty()) {
            throw new IllegalArgumentException("색은 비어있을 수 없습니다.");
        }
        if (color.length() > COLOR_LENGTH) {
            throw new IllegalArgumentException("색은 " + COLOR_LENGTH + "자를 초과할 수 없습니다.");
        }
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
