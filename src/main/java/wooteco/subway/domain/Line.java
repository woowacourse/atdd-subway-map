package wooteco.subway.domain;

public class Line {
    private static final String ERROR_MESSAGE_NAME_SIZE = "존재할 수 없는 이름입니다.";
    private static final String ERROR_MESSAGE_COLOR_SIZE = "존재할 수 없는 색상입니다.";
    private final int NAME_SIZE_LIMIT = 255;
    private final int COLOR_SIZE_LIMIT = 20;

    private Long id;
    private final String name;
    private final String color;

    public Line(String name, String color) {
        validateNameSize(name);
        validateColorSize(color);
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        validateNameSize(name);
        validateColorSize(color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateNameSize(String name) {
        if (name == null || name.isBlank() || name.length() > NAME_SIZE_LIMIT) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NAME_SIZE);
        }
    }

    private void validateColorSize(String color) {
        if (color == null || color.isBlank() || color.length() > COLOR_SIZE_LIMIT) {
            throw new IllegalArgumentException(ERROR_MESSAGE_COLOR_SIZE);
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
