package wooteco.subway.domain;

import wooteco.subway.exception.IllegalStationNameException;

public class Station {

    private final Long id;
    private final String name;

    public Station(final Long id, final String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new IllegalStationNameException();
        }
    }

    public Station(final String name) {
        this(null, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

