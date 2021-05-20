package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;

import java.util.Arrays;

import static wooteco.subway.station.StationFactory.*;

@DisplayName("기본 노선 생성")
public class LineFactory {

    public static final Line 인천1호선 = new Line(1L, "인천1호선", "bg-red-600",
            Arrays.asList(
                    new Section(흑기역, 백기역, 5),
                    new Section(백기역, 낙성대역, 8))
    );

    public static final Line 인천2호선 = new Line(2L, "인천2호선", "bg-red-500",
            Arrays.asList(
                    new Section(흑기역, 백기역, 5),
                    new Section(백기역, 낙성대역, 8))
    );
}
