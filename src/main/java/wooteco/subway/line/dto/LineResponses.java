package wooteco.subway.line.dto;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.line.Line;

public class LineResponses {

    public static List<LineResponse> toLineResponse(List<Line> lines) {
        return lines
            .stream()
            .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor(), null))
            .collect(Collectors.toList());
    }
}
