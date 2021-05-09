package wooteco.subway.station.domain;

import wooteco.subway.name.domain.Name;
import wooteco.subway.name.domain.StationName;

public class Station {
    private Long id;
    private Name name;

    public Station() {
    }

    public Station(final Long id, final String name) {
        this(id, new StationName(name));
    }

    public Station(final String name) {
        this(0L, new StationName(name));
    }

    public Station(final Long id, final Name name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.name();
    }

    public boolean sameName(final String name) {
        return this.name.sameName(name);
    }

    public boolean sameId(final Long id) {
        return this.id.equals(id);
    }
}

