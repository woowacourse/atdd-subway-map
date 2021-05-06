package wooteco.subway.station;

public class Station {
    private Long id;
    private StationName name;

    public Station() {
    }

    public Station(String name) {
        this.name = new StationName(name);
    }

    public Station(Long id, String name) {
        this(id, new StationName(name));
    }

    public Station(Long id, StationName name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
    }
}

