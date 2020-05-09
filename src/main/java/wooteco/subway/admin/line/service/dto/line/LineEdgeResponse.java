package wooteco.subway.admin.line.service.dto.line;

import com.fasterxml.jackson.annotation.JsonProperty;
import wooteco.subway.admin.line.domain.Line;
import wooteco.subway.admin.line.service.dto.edge.EdgeResponse;

import java.util.List;

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
