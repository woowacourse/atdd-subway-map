package wooteco.subway.dto;

public class StationRequest {
    private final String name;

    private StationRequest() {
        this(null);
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
