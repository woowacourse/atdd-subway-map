package wooteco.subway.fixture;

import static wooteco.subway.fixture.SectionFixture.sectionAB;
import static wooteco.subway.fixture.SectionFixture.sectionBC;
import static wooteco.subway.fixture.SectionFixture.sectionCD;
import static wooteco.subway.fixture.SectionFixture.sectionDE;

import java.util.List;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;

public class SectionsFixture {

    public static Sections createSections() {
        List<Section> sectionList = List.of(sectionAB, sectionBC, sectionCD, sectionDE);
        return new Sections(sectionList);
    }
}
