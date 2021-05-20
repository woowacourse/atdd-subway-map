package wooteco.subway.line;

import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;

import java.util.Arrays;

import static wooteco.subway.line.LineFactory.인천1호선;
import static wooteco.subway.line.LineFactory.인천2호선;
import static wooteco.subway.station.StationFactory.*;

public class SectionFactory {
    public static final Section 인천1호선_흑기백기구간 = new Section(1L,인천1호선, 흑기역, 백기역, 5);
    public static final Section 인천2호선_흑기백기구간 = new Section(2L,인천2호선, 흑기역, 백기역, 5);
    public static final Section 인천2호선_백기낙성대구간 =new Section(3L,인천2호선, 백기역, 낙성대역, 7);
    public static final Sections 인천1호선_구간 = new Sections(Arrays.asList(인천1호선_흑기백기구간));
    public static final Sections 인천2호선_구간 = new Sections(Arrays.asList(인천2호선_흑기백기구간, 인천2호선_백기낙성대구간));
}
