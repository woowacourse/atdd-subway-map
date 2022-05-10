package wooteco.subway.repository.entity;

import wooteco.subway.domain.Station;

public class StationEntity {

    private final Long id;
    private final String name;

    public StationEntity(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public StationEntity(final Station station) {
        this(station.getId(), station.getName());
    }

    public Station generateStation() {
        return new Station(id, name);
    }

    public StationEntity fillId(final Long id) {
        return new StationEntity(id, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
