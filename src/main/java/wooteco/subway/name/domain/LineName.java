package wooteco.subway.name.domain;

import java.util.regex.Pattern;

public class LineName implements Name {
    private static final Pattern PATTERN = Pattern.compile("^[가-힣|0-9]*선$");
    private final String name;

    public LineName(final String name) {
        validateName(name);
        this.name = name;
    }

    public void validateName(final String name) {
        if (!PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("잘못된 노선 이름입니다.");
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean sameName(final String name) {
        return this.name.equals(name);
    }

    @Override
    public Name changeName(final String name) {
        return new LineName(name);
    }
}
