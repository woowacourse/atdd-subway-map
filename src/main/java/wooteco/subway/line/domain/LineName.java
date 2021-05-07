package wooteco.subway.line.domain;

import java.util.regex.Pattern;

public class LineName {
    private static final Pattern LINE_NAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]*선$");

    private String value;

    public LineName(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (!LINE_NAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("지하철 노선 이름이 잘못되었습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
