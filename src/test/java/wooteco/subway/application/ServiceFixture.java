package wooteco.subway.application;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

public class ServiceFixture {

    public static final Line 분당선 = new Line(1L, "분당선", "노랑이");
    public static final Line 경의중앙선 = new Line(2L, "경의중앙선", "하늘이");
    public static final Station 강남역 = new Station(1L, "강남역");
    public static final Station 역삼역 = new Station(2L, "역삼역");
}
