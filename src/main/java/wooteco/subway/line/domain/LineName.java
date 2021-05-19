package wooteco.subway.line.domain;

import wooteco.subway.common.exception.InvalidNameException;

import java.util.Objects;
import java.util.regex.Pattern;

public class LineName {
    private static final Pattern PATTERN = Pattern.compile("^[가-힣|0-9]*선$");
    private String name;

    private LineName() {
    }

    public LineName(final String name) {
        validateName(name);
        this.name = name;
    }

    public static LineName emptyName() {
        return new LineName();
    }

    public void validateName(final String name) {
        if (!PATTERN.matcher(name).matches()) {
            throw new InvalidNameException("잘못된 노선 이름입니다.");
        }
    }

    public String name() {
        return name;
    }

    public boolean sameName(final String name) {
        return this.name.equals(name);
    }

    public LineName changeName(final String name) {
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
