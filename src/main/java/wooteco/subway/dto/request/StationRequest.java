package wooteco.subway.dto.request;

public class StationRequest {

    private String name;

    private StationRequest() {
    }

    public StationRequest(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "StationRequest{" +
                "name='" + name + '\'' +
                '}';
    }
}
