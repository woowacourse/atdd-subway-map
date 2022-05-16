package wooteco.subway.domain;

public class DomainFixtures {

    public static final Line 분당선 = new Line(1L, "분당선", "노랑이");
    public static final Line 호선2 = new Line(2L, "2호선", "초록이");
    public static final Station 왕십리역 = new Station(1L, "왕십리역");
    public static final Station 서울숲역 = new Station(2L, "서울숲역");
    public static final Station 강남역 = new Station(3L, "강남역");
    public static final Station 역삼역 = new Station(4L, "역삼역");
    public static final Section 분당선구간1 = new Section(1L, 분당선, 왕십리역, 서울숲역, 5);
    public static final Section 호선2구간1 = new Section(2L, 호선2, 강남역, 역삼역, 5);
}
