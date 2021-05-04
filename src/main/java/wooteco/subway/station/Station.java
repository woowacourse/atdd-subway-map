package wooteco.subway.station;

import wooteco.subway.name.Name;
import wooteco.subway.name.StationName;

public class Station {
    private Long id;
    private Name name;

    public Station() {
    }

    public Station(Long id, String name) {
        this(id, new StationName(name));
    }

    public Station(String name) {
        this(0L, new StationName(name));
    }

    public Station(Long id, Name name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.name();
    }

    public boolean sameName(String name) {
        return this.name.sameName(name);
    }
}

