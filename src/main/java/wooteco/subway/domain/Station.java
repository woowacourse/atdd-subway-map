package wooteco.subway.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class Station {

    private static final Pattern pattern = Pattern.compile("^[ㄱ-ㅎ|가-힣|0-9]+");
    private static final int MAX_RANGE = 10;
    private static final int MIN_RANGE = 2;

    private Long id;
    private String name;

    public Station(Long id, String name) {
        validateNameRange(name);
        validateLanguageType(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    private void validateNameRange(String name) {
        if (name.length() >= MAX_RANGE) {
            throw new IllegalArgumentException("지하철 역 이름은 10글자를 초과할 수 없습니다.");
        }

        if (name.length() < MIN_RANGE) {
            throw new IllegalArgumentException("지하철 역 이름은 2글자 이상이어야 합니다.");
        }
    }

    private void validateLanguageType(String name) {
        if (!pattern.matcher(name).matches()) {
            throw new IllegalArgumentException("지하철 역 이름은 한글과 숫자이어야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Station)) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Station{" +
            "name='" + name + '\'' +
            '}';
    }
}

