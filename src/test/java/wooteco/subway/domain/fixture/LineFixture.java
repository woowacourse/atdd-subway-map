package wooteco.subway.domain.fixture;

import java.util.List;

import wooteco.subway.domain.Line;

public class LineFixture {

    public static final Line LINE_AB = new Line(1L,
        "분당선",
        "RED",
        List.of(SectionFixture.SECTION_AB)
    );

}
