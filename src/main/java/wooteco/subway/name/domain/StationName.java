package wooteco.subway.name.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class StationName implements Name {
    private static final Pattern PATTERN = Pattern.compile("^[가-힣|0-9]*역$");
    private String name;

    public StationName() {
    }

    public StationName(final String name) {
        validateName(name);
        this.name = name;
    }

    public void validateName(final String name) {
        if (!PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("잘못된 역 이름입니다.");
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
    public Name changeName(String name) {
        return new StationName(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationName that = (StationName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
