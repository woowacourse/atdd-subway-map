package wooteco.subway.domain;

import java.util.Objects;

public class Station {
    private Long id;
    private String name;

    public Station(String name) {
        this(null, name);
    }

    public Station(Long id, String name) {
        validateArgument(name);

        this.id = id;
        this.name = name;
    }

    private void validateArgument(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("지하철역 이름이 공백일 수 없습니다.");
        }
        if (name.length() >= 255) {
            throw new IllegalArgumentException("지하철역 이름이 너무 깁니다.");
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

