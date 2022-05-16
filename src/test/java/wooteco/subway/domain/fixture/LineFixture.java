package wooteco.subway.domain.fixture;

import java.util.List;

import wooteco.subway.domain.line.Line;

public class LineFixture {

    public static Line getLineAb() {
        return new Line(1L,
            "분당선",
            "RED",
            List.of(SectionFixture.getSectionAb())
        );
    }

    public static Line getNewLine() {
        final Line line = new Line("새로운역", "새로운색");
        line.addSection(SectionFixture.getSectionAc());
        return line;
    }
}
