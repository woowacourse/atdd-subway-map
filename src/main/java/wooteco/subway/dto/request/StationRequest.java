package wooteco.subway.dto.request;

public class StationRequest {

    private String name;

    private StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
