package wooteco.subway.domain;

import java.util.Objects;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    public boolean isSameStation(Station other) {
        return this.name.equals(other.name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

