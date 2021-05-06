package wooteco.subway.line;

import wooteco.subway.exception.line.LineNamePatternException;

import java.util.regex.Pattern;

public class LineName {

    private static final Pattern PATTERN = Pattern.compile("^[가-힣|A-Z|a-z| 0-9]*선$");

    private final String name;

    public LineName(String name) {
        validate(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private void validate(String name) {
        if (!PATTERN.matcher(name).matches()) {
            throw new LineNamePatternException();
        }
    }
}
