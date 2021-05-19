package wooteco.subway.station;

public class StationResponse {

    private Long id;
    private String name;

    public StationResponse() {
    }

    private StationResponse(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse from(final Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public static StationResponse of(final Long id, final String name) {
        return new StationResponse(id, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
