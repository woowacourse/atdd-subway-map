package wooteco.subway.dto.request;

public class StationSaveRequest {

    private final String name;

    private StationSaveRequest(String name) {
        this.name = name;
    }

    public static StationSaveRequest of(String name) {
        return new StationSaveRequest(name);
    }

    public String getName() {
        return name;
    }
}
