package wooteco.subway.station;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationResponse(Station station) {
        this(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
