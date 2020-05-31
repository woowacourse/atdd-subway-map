package wooteco.subway.admin.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import wooteco.subway.admin.domain.Station;

public class StationCreateRequest {

    @NotEmpty(message = "빈스트링 또는 null 값이 들어올 수 없습니다.")
    @Pattern(regexp = "^([^0-9]*)$", message = "숫자가 들어올 수 없습니다.")
    @Pattern(regexp = "^\\S", message = "공백이 들어올 수 없습니다.")
    private String name;

    public Station toStation() {
        return new Station(name);
    }

    public String getName() {
        return name;
    }
}
