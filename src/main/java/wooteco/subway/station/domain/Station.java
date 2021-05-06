package wooteco.subway.station.domain;

public class Station {
    private Long id;
    private final String name;

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Station(final String name) {
        this.name = name;
    }

    public boolean isSameName(final Station station) {
        return this.name.equals(station.name);
    }

    public boolean isSameId(final Long id) {
        return this.id.equals(id);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

