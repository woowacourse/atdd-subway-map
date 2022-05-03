package wooteco.subway.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class Station {

    private static final Pattern pattern = Pattern.compile("[ㄱ-ㅎ|가-힣|0-9]");

    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        this(name);
        this.id = id;
    }

    public Station(String name) {
        validateNameRange(name);
        validateLanguageType(name);
        this.name = name;
    }

    private void validateNameRange(String name) {
        if (name.length() >= 10) {
            throw new IllegalArgumentException("지하철 역 이름은 10글자를 초과할 수 없습니다.");
        }

        if (name.length() < 2) {
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
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

