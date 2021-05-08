package wooteco.subway.name.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class LineName implements Name {
    private static final Pattern PATTERN = Pattern.compile("^[가-힣|0-9]*선$");
    private String name;

    public LineName() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineName lineName = (LineName) o;
        return Objects.equals(name, lineName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
