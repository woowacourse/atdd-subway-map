package wooteco.subway.domain;

import java.util.regex.Pattern;

public class Line {

    private static final Pattern pattern = Pattern.compile("^[ㄱ-ㅎ|가-힣|0-9]+");
    private static final int MAX_RANGE = 10;
    private static final int MIN_RANGE = 3;

    private Long id;
    private String name;
    private String color;

    public Line(String name, String color) {
        validateNameRange(name);
        validateLanguageType(name);
        this.name = name;
        this.color = color;
    }

    public void update(String name, String color) {
        validateNameRange(name);
        validateLanguageType(name);
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
}
