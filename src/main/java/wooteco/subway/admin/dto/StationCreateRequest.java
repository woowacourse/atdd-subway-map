package wooteco.subway.admin.dto;


import javax.validation.constraints.Pattern;
import wooteco.subway.admin.domain.Station;

public class StationCreateRequest {
    @Pattern(regexp = "^(?=^\\D+$)\\S+$", message = "이름은 공백 및 숫자를 포함할 수 없다.")
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
}
