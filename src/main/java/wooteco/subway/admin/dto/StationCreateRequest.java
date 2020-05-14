package wooteco.subway.admin.dto;


import javax.validation.constraints.NotNull;
import wooteco.subway.admin.domain.Station;

public class StationCreateRequest {

    @NotNull
    private String name;

    public StationCreateRequest() {
    }

    public StationCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }

    @Override
    public String toString() {
        return "StationCreateRequest{" +
            "name='" + name + '\'' +
            '}';
    }
}
