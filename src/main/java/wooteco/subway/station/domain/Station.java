package wooteco.subway.station.domain;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Station(final String name) {
        this.name = name;
    }

    public boolean isSameName(Station station) {
        return this.name.equals(station.name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

