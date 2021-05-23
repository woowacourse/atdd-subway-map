package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineName;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;

import java.util.Arrays;
import static wooteco.subway.station.StationFactory.*;

@DisplayName("기본 노선 생성")
public class LineFactory {

    public static final Line 구간없는_인천1호선 = new Line(2L, new LineName("인천1호선"), "bg-red-600");
    public static final Line 구간없는_인천2호선 = new Line(3L, new LineName("인천2호선"), "bg-red-500");

    public static final Section 인천1호선_흑기백기구간 = new Section(1L, 구간없는_인천1호선, 흑기역, 백기역, 5);
    public static final Section 인천2호선_흑기백기구간 = new Section(2L, 구간없는_인천2호선, 흑기역, 백기역, 5);
    public static final Section 인천2호선_백기낙성대구간 = new Section(3L, 구간없는_인천2호선, 백기역, 낙성대역, 7);

    public static final Sections 인천1호선_구간 = new Sections(Arrays.asList(인천1호선_흑기백기구간));
    public static final Sections 인천2호선_구간 = new Sections(Arrays.asList(인천2호선_흑기백기구간, 인천2호선_백기낙성대구간));

    public static final Line 인천1호선 = new Line(구간없는_인천1호선.id(), new LineName(구간없는_인천1호선.name()), 구간없는_인천1호선.color(), 인천1호선_구간);
    public static final Line 인천2호선 = new Line(구간없는_인천2호선.id(), new LineName(구간없는_인천2호선.name()), 구간없는_인천2호선.color(), 인천2호선_구간);
}
