package wooteco.subway.domain;

import java.util.Objects;

public class Station {
    private Long id;
    private String name;

    private Station() {
    }

    public Station(Long id) {
        this.id = id;
        this.name = "";
    }

    public Station(String name) {
        this.name = name;
    }

    public Station(Long id, String name) {
        this(name);
        this.id = id;
    }

    public boolean isSameStation(Long stationId) {
        return Objects.equals(id, stationId);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

