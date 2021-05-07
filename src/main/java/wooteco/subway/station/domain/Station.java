package wooteco.subway.station.domain;

public class Station {
    private Long id;
    private StationName name;

    public Station() {
    }

    public Station(String name) {
        this(null, name);
    }

    public Station(Long id, StationName name) {
        this.id = id;
        this.name = name;
    }

    public Station(Long id, String name) {
        this(id, new StationName(name));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }
}

