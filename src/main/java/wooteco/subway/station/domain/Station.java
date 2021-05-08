package wooteco.subway.station.domain;

public class Station {
    private Long id;
    private String name;

    private Station() {
    }

    private Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private Station(String name) {
        this.name = name;
    }

    public static Station from(String name) {
        return new Station(name);
    }

    public static Station of(long id, String name) {
        return new Station(id, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

