package wooteco.subway.admin.line.service.dto.edge;

import wooteco.subway.admin.line.domain.edge.Edge;
import wooteco.subway.admin.station.domain.Stations;

import java.util.List;
import java.util.stream.Collectors;

public class EdgeResponse {
    private Long preStationId;
    private Long stationId;
    private String stationName;

    protected EdgeResponse() {
    }

    public EdgeResponse(final Long preStationId, final Long stationId, final String stationName) {
        this.preStationId = preStationId;
        this.stationId = stationId;
        this.stationName = stationName;
    }

    public static List<EdgeResponse> listOf(final List<Edge> edges, final Stations stations) {
        return edges.stream()
                .map(edge -> EdgeResponse.of(edge, stations))
                .collect(Collectors.toList());
    }

    public static EdgeResponse of(final Edge edge, final Stations stations) {
        Long preStationId = edge.getPreStationId();
        Long stationId = edge.getStationId();
        String stationName = stations.findNameById(stationId);
        return new EdgeResponse(preStationId, stationId, stationName);
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Long getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }
}
