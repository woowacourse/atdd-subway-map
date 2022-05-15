package wooteco.subway.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class Line {

    private static final Pattern pattern = Pattern.compile("^[ㄱ-ㅎ|가-힣|0-9]+");
    private static final int MAX_RANGE = 10;
    private static final int MIN_RANGE = 3;

    private final Long id;
    private String name;
    private String color;

    public Line(Long id, String name, String color) {
        validate(name, color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public void validateUpdate(String name, String color) {
        validate(name, color);
        this.name = name;
        this.color = color;
    }

    private void validate(String name, String color) {
        validateEmpty(name, color);
        validateNameRange(name);
        validateLanguageType(name);
    }

    private void validateEmpty(String name, String color) {
        if (name.isBlank() || color.isBlank()) {
            throw new IllegalArgumentException("이름과 색깔은 공백일 수 없습니다.");
        }
    }

    private void validateNameRange(String name) {
        if (name.length() >= MAX_RANGE) {
            throw new IllegalArgumentException(String.format("노선 이름은 %d글자를 초과할 수 없습니다.", MAX_RANGE));
        }

        if (name.length() < MIN_RANGE) {
            throw new IllegalArgumentException(String.format("노선 이름은 %d글자 이상이어야 합니다.", MIN_RANGE));
        }
    }

    private void validateLanguageType(String name) {
        if (!pattern.matcher(name).matches()) {
            throw new IllegalArgumentException("노선 이름은 한글과 숫자이어야 합니다.");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Line)) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects
                .equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
