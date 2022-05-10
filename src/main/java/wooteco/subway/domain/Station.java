package wooteco.subway.domain;

import wooteco.subway.exception.StationNameEmptyException;

public class Station {

    private Long id;
    private String name;

    public Station(String name) {
        this(null, name);
    }

    public Station(final Long id, final String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new StationNameEmptyException();
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
