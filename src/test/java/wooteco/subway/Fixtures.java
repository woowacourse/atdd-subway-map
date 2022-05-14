package wooteco.subway;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.SectionV2;
import wooteco.subway.domain.Station;

public class Fixtures {

    // Station Fixture
    public static final Station 강남역 = new Station(1L,"강남역");
    public static final Station 역삼역 = new Station(2L, "역삼역");
    public static final Station 선릉역 = new Station(3L,"선릉역");
    public static final Station 삼성역 = new Station(4L,"삼성역");

    // Line Fixture
    public static final Line 이호선 = new Line(1L, "2호선", "초록색");

    // Section Fixture
    public static final SectionV2 강남_역삼 = new SectionV2(1L, 강남역, 역삼역, 5);
    public static final SectionV2 강남_선릉 = new SectionV2(1L, 강남역, 선릉역, 10);
    public static final SectionV2 역삼_선릉 = new SectionV2(1L, 역삼역, 선릉역, 5);
    public static final SectionV2 강남_삼성 = new SectionV2(1L, 강남역, 삼성역, 15);
    public static final SectionV2 선릉_삼성 = new SectionV2(1L, 선릉역, 삼성역, 5);
    public static final SectionV2 역삼_삼성 = new SectionV2(1L, 역삼역, 삼성역, 10);
}
