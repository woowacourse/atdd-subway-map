package wooteco.subway.domain.fixture;

import static wooteco.subway.domain.fixture.StationFixture.*;

import wooteco.subway.domain.property.Distance;
import wooteco.subway.domain.section.Section;

public class SectionFixture {

    public static Section getSectionAb() {
        return new Section(1L, getStationA(), getStationB(), new Distance(7));
    }

    public static Section getSectionBc() {
        return new Section(2L, getStationB(), getStationC(), new Distance(8));
    }

    public static Section getSectionAc() {
        return new Section(3L, getStationA(), getStationC(), new Distance(5));
    }

    public static Section getSectionXy() {
        return new Section(3L, getStationX(), getStationY(), new Distance(5));
    }
}
