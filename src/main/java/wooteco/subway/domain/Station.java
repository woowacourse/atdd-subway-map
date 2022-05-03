package wooteco.subway.domain;

import wooteco.subway.exception.EmptyNameException;

public class Station {
    private Long id;
    private String name;

    public Station(Long id, String name) {
        if (name.isBlank()) {
            throw new EmptyNameException();
        }

        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }
}

