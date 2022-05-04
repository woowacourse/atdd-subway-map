package wooteco.subway.domain;

import java.util.Objects;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Station(final String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

