package wooteco.subway.admin.station.service.dto;


import javax.validation.constraints.NotNull;

public class StationCreateRequest {
    @NotNull(message = "역 이름이 비어있습니다.")
    private String name;

    private StationCreateRequest() {
    }

    public String getName() {
        return name;
    }
}
