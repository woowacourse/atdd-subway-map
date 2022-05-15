package wooteco.subway.dao;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

public class DaoFixtures {

    public static final Station 강남역 = new Station("강남역");
    public static final Station 역삼역 = new Station("역삼역");
    public static final Line 분당선 = new Line("분당선", "노랑");
    public static final Line 호선2 = new Line("2호선", "초록");
}
