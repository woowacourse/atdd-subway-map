package wooteco.subway.dto.request;

import java.util.Objects;

public class SectionRequest {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        Objects.requireNonNull(upStationId, ERROR_NULL);
        Objects.requireNonNull(downStationId, ERROR_NULL);
        Objects.requireNonNull(distance, ERROR_NULL);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
