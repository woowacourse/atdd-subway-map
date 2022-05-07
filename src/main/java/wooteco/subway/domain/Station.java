package wooteco.subway.domain;

public class Station {
    private Long id;
    private String name;

    private Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Station(Long id, String name) {
        this(name);
        this.id = id;
    }

    public boolean isSameName(Station station) {
        return name.equals(station.name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

