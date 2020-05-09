package wooteco.subway.admin.line.service.dto.edge;

import wooteco.subway.admin.line.domain.edge.Edge;
import wooteco.subway.admin.station.domain.Station;

import java.util.List;
import java.util.Set;
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

    public static List<EdgeResponse> listOf(final List<Edge> edges, final Set<Station> stations) {
        return edges.stream()
                .map(edge -> EdgeResponse.of(edge, stations))
                .collect(Collectors.toList());
    }

    public static EdgeResponse of(final Edge edge, final Set<Station> stations) {
        Long preStationId = edge.getPreStationId();
        Long stationId = edge.getStationId();
        String stationName = stations.stream()
                .filter(station -> station.isSameId(stationId))
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getName();
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
