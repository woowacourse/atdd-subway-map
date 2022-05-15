package wooteco.subway.service.dto;

public class StationServiceRequest {

    private final String name;

    public StationServiceRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
