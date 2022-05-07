package wooteco.subway.dto;

import javax.validation.constraints.Min;

public class SectionSaveRequest {

    private long upStationId;
    private long downStationId;

    @Min(value = 1, message = "상행-하행 노선 길이는 1 이상의 값만 들어올 수 있습니다.")
    private int distance;

    private SectionSaveRequest() {
    }

    public SectionSaveRequest(final long upStationId, final long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}