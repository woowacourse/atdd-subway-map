package wooteco.subway.admin.line.service.dto.edge;

import wooteco.subway.admin.line.domain.edge.Edge;

import javax.validation.constraints.NotNull;

public class EdgeCreateRequest {
    @NotNull(message = "이전 역 값이 비어있습니다.")
    private Long preStationId;
    @NotNull(message = "대상 역 값이 비어있습니다.")
    private Long stationId;
    @NotNull(message = "구간 값이 비어있습니다.")
    private Integer distance;
    @NotNull(message = "기간 값이 비어있습니다.")
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

    public Edge toEdge() {
        return new Edge(this.preStationId, this.stationId, this.distance, this.duration);
    }
}
