package wooteco.subway.domain;

import wooteco.subway.exception.Validator;

public class Station {

    private Long id;
    private String name;

    public Station(final Long id, final String name) {
        Validator.requireNonNull(name);
        this.id = id;
        this.name = name;
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

