package wooteco.subway.station.controller.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import wooteco.subway.station.service.dto.StationCreateDto;

public class StationRequest {

    @Pattern(regexp = "^[가-힣a-zA-Z0-9]*역$", message = "지하철 역 이름은 'XX역' 으로 끝나야합니다.")
    @Size(min=3, max=12, message = "지하철 역 이름은 최소 3글자, 최대 12글자로 이루어져야합니다.")
    private String name;

    public StationRequest() {
    }

    public StationRequest(final String name) {
        this.name = name;
    }

    public StationCreateDto toStationCreateDto() {
        return StationCreateDto.of(name);
    }

    public String getName() {
        return name;
    }
}
