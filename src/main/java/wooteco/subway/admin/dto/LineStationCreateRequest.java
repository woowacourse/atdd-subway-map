package wooteco.subway.admin.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LineStationCreateRequest {

    @NotNull(message = "노선은 필수입력 항목입니다.")
    private Long lineId;

    private String preStationName;

    @NotEmpty(message = "대상역은 필수입력 항목입니다.")
    private String stationName;

    public LineStationCreateRequest() {
    }

    public LineStationCreateRequest(Long lineId, String preStationName, String stationName) {
        this.lineId = lineId;
        this.preStationName = preStationName;
        this.stationName = stationName;
    }

    public Long getLineId() {
        return lineId;
    }

    public String getPreStationName() {
        return preStationName;
    }

    public String getStationName() {
        return stationName;
    }
}
