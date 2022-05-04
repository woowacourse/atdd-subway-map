package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.BlankArgumentException;

public class Station {

    private final Long id;
    private final String name;

    public Station(Long id, String name) {
        if (name.isBlank()) {
            throw new BlankArgumentException();
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

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }
}

