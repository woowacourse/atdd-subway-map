package wooteco.subway.domain;

public class Station {

    private final Long id;
    private final String name;

    public Station(String name) {
        this(null, name);
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean hasSameName(Station station) {
        return this.name.equals(station.name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
