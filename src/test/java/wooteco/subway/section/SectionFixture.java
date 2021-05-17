package wooteco.subway.section;

import static wooteco.subway.line.LineFixture.이호선;
import static wooteco.subway.station.StationFixture.*;

public class SectionFixture {
    public static Section 이호선_왕십리_잠실_거리10 = new Section(1L, 이호선, 왕십리역, 잠실역, 10);
    public static Section 이호선_잠실_강남_거리5 = new Section(2L, 이호선, 잠실역, 강남역, 5);
    public static Section 이호선_강남_구의_거리7 = new Section(3L, 이호선, 강남역, 구의역, 7);
}
