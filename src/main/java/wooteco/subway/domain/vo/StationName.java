package wooteco.subway.domain.vo;

import java.util.Objects;

public class StationName {

    private String name;

    private StationName(String name) {
        this.name = name;
    }

    public static StationName of(String name) {
        validateArgument(name);

        return new StationName(name);
    }

    private static void validateArgument(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("지하철역 이름이 공백일 수 없습니다.");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("지하철역 이름은 255자 보다 클 수 없습니다.");
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StationName that = (StationName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
