package wooteco.subway.station;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public final Long getId() {
        return id;
    }

    public final String getName() {
        return name;
    }
}

