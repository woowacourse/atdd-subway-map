package wooteco.subway.admin.dto;

import javax.validation.constraints.NotBlank;

public class LineStationCreateRequest {

    private String preStationName;

    @NotBlank(message = "대상역은 필수입력 항목입니다.")
    private String stationName;

    public LineStationCreateRequest() {
    }

    public LineStationCreateRequest(String preStationName, String stationName) {
        this.preStationName = preStationName;
        this.stationName = stationName;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public String getStationName() {
        return stationName;
    }
}
