package wooteco.subway.station.domain;

import wooteco.subway.common.exception.InvalidInputException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StationName {
    private static final Pattern PATTERN = Pattern.compile("^[가-힣|0-9]*역$");
    private String name;

    private StationName() {
    }

    public StationName(final String name) {
        validateName(name);
        this.name = name;
    }

    public static StationName emptyName() {
        return new StationName();
    }

    public void validateName(final String name) {
        Matcher matcher = PATTERN.matcher(name);
        if (!matcher.matches()) {
            throw new InvalidInputException("잘못된 역 이름입니다.");
        }
    }

    public String name() {
        return name;
    }

    public boolean sameName(final String name) {
        return this.name.equals(name);
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
