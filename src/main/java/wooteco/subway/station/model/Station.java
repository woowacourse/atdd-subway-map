package wooteco.subway.station.model;

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

    public boolean isSameName(Station newStation) {
        return this.name.equals(newStation.name);
    }
}

