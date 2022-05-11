package wooteco.subway.domain.fixture;

import static wooteco.subway.domain.fixture.StationFixture.*;

import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Section;

public class SectionFixture {
    public static final Section SECTION_AB = new Section(1L, STATION_A, STATION_B, new Distance(7));
    public static final Section SECTION_BC = new Section(2L, STATION_B, STATION_C, new Distance(8));
    public static final Section SECTION_AC = new Section(3L, STATION_A, STATION_C, new Distance(5));
    public static final Section SECTION_XY = new Section(3L, STATION_X, STATION_Y, new Distance(5));
}
