package wooteco.subway.station.domain;

public class Station {
    private final Long id;
    private final String name;

    public Station(final String name) {
        this(null, name);
    }

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isSameName(final String name) {
        return this.name.equals(name);
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

