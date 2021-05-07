package wooteco.subway.line.dto;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.line.Line;

public class LineResponses {

    private List<LineResponse> responses;

    public LineResponses() {
    }

    private LineResponses(List<LineResponse> responses) {
        this.responses = responses;
    }

    public static LineResponses from(List<Line> lines) {
        return new LineResponses(lines
            .stream()
            .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor(), null))
            .collect(Collectors.toList()));
    }

    public List<LineResponse> toList() {
        return responses;
    }
}
