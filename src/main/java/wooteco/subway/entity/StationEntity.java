package wooteco.subway.entity;

import wooteco.subway.domain.station.Station;

public class StationEntity implements Entity {

    private final Long id;
    private final String name;

    public StationEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationEntity(String name) {
        this(null, name);
    }

    public static StationEntity from(Station station) {
        return new StationEntity(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
