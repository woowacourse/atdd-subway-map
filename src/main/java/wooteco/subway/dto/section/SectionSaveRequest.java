package wooteco.subway.dto.section;

import javax.validation.constraints.Positive;

public class SectionSaveRequest {

    @Positive(message = "상행역의 id는 양수 값만 들어올 수 있습니다.")
    private long upStationId;

    @Positive(message = "하행역의 id는 양수 값만 들어올 수 있습니다.")
    private long downStationId;

    @Positive(message = "상행-하행 노선 길이는 양수 값만 들어올 수 있습니다.")
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
