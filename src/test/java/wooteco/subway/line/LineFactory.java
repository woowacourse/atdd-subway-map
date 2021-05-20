package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineName;

import static wooteco.subway.line.SectionFactory.인천1호선_구간;
import static wooteco.subway.line.SectionFactory.인천2호선_구간;

@DisplayName("기본 노선 생성")
public class LineFactory {
    public static final Line 인천1호선 = new Line(1L, new LineName("인천1호선"), "bg-red-600", 인천1호선_구간);
    public static final Line 인천2호선 = new Line(1L, new LineName("인천2호선"), "bg-red-500", 인천2호선_구간);
}
