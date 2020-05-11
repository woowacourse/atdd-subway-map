package wooteco.subway.admin.line.service.dto.edge;

import wooteco.subway.admin.line.domain.edge.Edge;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EdgeCreateRequest {
    @NotNull(message = "이전 역 값이 비어있습니다.")
    private Long preStationId;
    @NotNull(message = "대상 역 값이 비어있습니다.")
    private Long stationId;
    @Min(value = 1, message = "구간 값을 0이상 입력해 주십시오.")
    private Integer distance;
    @Min(value = 1, message = "기간 값을 1이상 입력해 주십시오.")
    private Integer duration;

    public EdgeCreateRequest() {
    }

    public EdgeCreateRequest(Long preStationId, Long stationId, int distance, int duration) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.distance = distance;
        this.duration = duration;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }

    public List<Long> getAllStationId() {
        if (Objects.isNull(preStationId)) {
            return Collections.singletonList(stationId);
        }
        return new ArrayList<>(Arrays.asList(preStationId, stationId));
    }

    public Edge toEdge() {
        return new Edge(this.preStationId, this.stationId, this.distance, this.duration);
    }
}
