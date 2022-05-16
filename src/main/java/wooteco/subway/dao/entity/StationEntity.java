package wooteco.subway.dao.entity;

import wooteco.subway.domain.Station;

public class StationEntity {

    private final Long id;
    private final String name;

    public StationEntity(final String name) {
        this(null, name);
    }

    public StationEntity(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static StationEntity from(final Station station) {
        return new StationEntity(station.getName());
    }

    public Station toStation() {
        return new Station(id, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
