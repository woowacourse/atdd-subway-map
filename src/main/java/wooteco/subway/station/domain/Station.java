package wooteco.subway.station.domain;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean equalName(Station station) {
        return this.name.equals(station.name);
    }

    public boolean equalId(Long id) {
        return this.id.equals(id);
    }
}

