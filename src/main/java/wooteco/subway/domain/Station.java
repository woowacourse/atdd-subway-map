package wooteco.subway.domain;

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

    public boolean isSameName(String stationName) {
        return name.equals(stationName);
    }

    public boolean isSameId(Long stationId) {
        return id == stationId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

