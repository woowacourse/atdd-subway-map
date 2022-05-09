package wooteco.subway.entity;

public class StationEntity {

    private final String name;

    private StationEntity(String name) {
        this.name = name;
    }

    public static StationEntity of(String name) {
        return new StationEntity(name);
    }

    public String getName() {
        return name;
    }
}
