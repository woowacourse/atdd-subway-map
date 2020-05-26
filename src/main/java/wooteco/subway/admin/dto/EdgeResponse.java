package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Edge;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EdgeResponse {
    private Long stationId;
    private Long preStationId;
    private int distance;
    private int duration;

    public EdgeResponse() {

    }

    public EdgeResponse(Long stationId, Long preStationId, int distance, int duration) {
        this.stationId = stationId;
        this.preStationId = preStationId;
        this.distance = distance;
        this.duration = duration;
    }

    public static List<EdgeResponse> of(Set<Edge> edges) {
        return edges.stream()
                .map(it -> EdgeResponse.of(it))
                .collect(Collectors.toList());
    }

    public static EdgeResponse of(Edge edge) {
        return new EdgeResponse(edge.getStationId(), edge.getPreStationId(), edge.getDistance(), edge.getDistance());
    }

    public Long getStationId() {
        return stationId;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }
}
