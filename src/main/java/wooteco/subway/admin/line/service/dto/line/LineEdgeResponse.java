package wooteco.subway.admin.line.service.dto.line;

import com.fasterxml.jackson.annotation.JsonProperty;
import wooteco.subway.admin.line.domain.Line;
import wooteco.subway.admin.line.domain.Lines;
import wooteco.subway.admin.line.service.dto.edge.EdgeResponse;
import wooteco.subway.admin.station.domain.Stations;

import java.util.List;
import java.util.stream.Collectors;

public class LineEdgeResponse {
    private Long id;
    @JsonProperty("title")
    private String name;
    private String bgColor;
    private List<EdgeResponse> edges;

    public LineEdgeResponse(final Line line, final List<EdgeResponse> edges) {
        this.id = line.getId();
        this.name = line.getName();
        this.bgColor = line.getColor();
        this.edges = edges;
    }

    public static List<LineEdgeResponse> listOf(final Lines lines, final Stations stations) {
        return lines.stream()
                .map(line -> new LineEdgeResponse(line, EdgeResponse.listOf(line.getEdges(), stations)))
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBgColor() {
        return bgColor;
    }

    public List<EdgeResponse> getEdges() {
        return edges;
    }
}
