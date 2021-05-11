package wooteco.subway.line;

import wooteco.subway.line.controller.dto.LineRequest;

public class LineTestData {

    public static LineRequest requestWithUpDownStations() {
        return new LineRequest(
                "신분당선",
                "bg-red-600",
                1L,
                2L,
                10
        );
    }
}
