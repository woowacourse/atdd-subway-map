package wooteco.subway.fixture;

import static wooteco.subway.fixture.StationFixture.AStation;
import static wooteco.subway.fixture.StationFixture.BStation;
import static wooteco.subway.fixture.StationFixture.CStation;
import static wooteco.subway.fixture.StationFixture.DStation;
import static wooteco.subway.fixture.StationFixture.EStation;

import java.util.List;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public class SectionsFixture {

    public static Sections createSections() {
        Section ab = new Section(1L, 1L, AStation, BStation, 10);
        Section bc = new Section(2L, 1L, BStation, CStation, 10);
        Section cd = new Section(3L, 1L, CStation, DStation, 10);
        Section de = new Section(4L, 1L, DStation, EStation, 10);

        List<Section> sectionList = List.of(ab, bc, cd, de);
        return new Sections(sectionList);
    }
}
