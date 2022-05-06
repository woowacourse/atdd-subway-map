package wooteco.subway.domain;

public class Station {

    private final Name name;
    private Long id;

    public Station(final String name) {
        this.name = new Name(name);
    }

    public boolean isSameName(final Station station) {
        return name.equals(station.name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }
}

