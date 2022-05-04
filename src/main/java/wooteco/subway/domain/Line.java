package wooteco.subway.domain;

public class Line {

    private static final int MAX_RANGE = 10;
    private static final int MIN_RANGE = 3;

    private Long id;
    private String name;
    private String color;

    public Line(String name, String color) {
        validateNameRange(name);
        this.name = name;
        this.color = color;
    }

    private void validateNameRange(String name) {
        if (name.length() >= MAX_RANGE) {
            throw new IllegalArgumentException("노선 이름은 10글자를 초과할 수 없습니다.");
        }

        if (name.length() < MIN_RANGE) {
            throw new IllegalArgumentException("노선 이름은 3글자 이상이어야 합니다.");
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
