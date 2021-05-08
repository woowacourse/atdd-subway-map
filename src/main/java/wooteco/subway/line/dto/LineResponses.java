package wooteco.subway.line.dto;

import wooteco.subway.line.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponses {
    private final List<LineResponse> lineResponses;

    public static LineResponses of(List<Line> lines) {
        return new LineResponses(lines.stream()
                                      .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                                      .collect(Collectors.toList()));
    }

    public LineResponses(List<LineResponse> lineResponses) {
        this.lineResponses = lineResponses;
    }

    public List<LineResponse> getLineResponses() {
        return lineResponses;
    }
}
