package wooteco.subway.service.dto.station;

public class StationSaveRequest {

    private String name;

    public StationSaveRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
