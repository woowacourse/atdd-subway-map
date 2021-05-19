package wooteco.subway.station.domain;

public class Station {
    private Long id;
    private String name;

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Station(final String name) {
        this(null, name);
    }

    public final Long getId() {
        return id;
    }

    public final String getName() {
        return name;
    }
}

