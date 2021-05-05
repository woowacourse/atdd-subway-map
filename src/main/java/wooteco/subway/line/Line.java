package wooteco.subway.line;

import java.util.Objects;
import java.util.regex.Pattern;

public class Line {
    private static final int MAX_NAME_LENGTH = 20;
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[·가-힣a-zA-Z0-9\\(\\)\\s]*$");

    private Long id;
    private String name;
    private String color;

    public Line() {
        this(null, null, null);
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, final String name, String color) {
        String trimAndRemoveDuplicateBlankName = name.trim().replaceAll(" +", " ");
        validateNameLength(trimAndRemoveDuplicateBlankName);
        validateInvalidName(trimAndRemoveDuplicateBlankName);
        this.id = id;
        this.name = trimAndRemoveDuplicateBlankName;
        this.color = color;
    }

    private void validateNameLength(String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(String.format("노선 이름은 %d자를 초과할 수 없습니다. 이름의 길이 : %d", MAX_NAME_LENGTH, name.length()));
        }
    }

    private void validateInvalidName(String name) {
        if (!VALID_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException(String.format("노선 이름에 유효하지 않은 문자가 있습니다. 노선 이름 : %s", name));
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

