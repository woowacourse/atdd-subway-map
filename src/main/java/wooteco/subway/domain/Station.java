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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

