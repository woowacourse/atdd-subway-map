package wooteco.subway.fixture;

import static wooteco.subway.fixture.StationFixture.stationA;
import static wooteco.subway.fixture.StationFixture.stationB;
import static wooteco.subway.fixture.StationFixture.stationC;
import static wooteco.subway.fixture.StationFixture.stationD;
import static wooteco.subway.fixture.StationFixture.stationE;

import wooteco.subway.domain.Section;

public class SectionFixture {

    public static Section sectionAB = new Section(1L, 1L, stationA, stationB, 10);
    public static Section sectionBC = new Section(2L, 1L, stationB, stationC, 10);
    public static Section sectionCD = new Section(3L, 1L, stationC, stationD, 10);
    public static Section sectionDE = new Section(4L, 1L, stationD, stationE, 10);
}
