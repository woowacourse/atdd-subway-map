package wooteco.subway.domain;

import static wooteco.subway.domain.LineFixtures.*;
import static wooteco.subway.domain.StationFixtures.*;

public class SectionFixtures {

    public static final Section 신도림역_온수역_5 = new Section(1L, LINE_1, 신도림역, 온수역, 5);
    public static final Section 온수역_역곡역_5 = new Section(2L, LINE_1, 온수역, 역곡역, 5);
    public static final Section 역곡역_부천역_5 = new Section(3L, LINE_1, 역곡역, 부천역, 5);
    public static final Section 부천역_중동역_5 = new Section(4L, LINE_1, 부천역, 중동역, 5);

    public static final Section 부천역_역곡역_5 = new Section(5L, LINE_1, 부천역, 역곡역, 5);
    public static final Section 중동역_역곡역_5 = new Section(6L, LINE_1, 중동역, 역곡역, 5);

    public static final Section 신도림역_부천역_15 = new Section(7L, LINE_1, 신도림역, 부천역, 15);
    public static final Section 신도림역_역곡역_13 = new Section(8L, LINE_1, 신도림역, 역곡역, 13);
    public static final Section 신도림역_개봉역_10 = new Section(9L, LINE_1, 신도림역, 개봉역, 10);
    public static final Section 소사역_부천역_5 = new Section(10L, LINE_1, 소사역, 부천역, 5);
}
