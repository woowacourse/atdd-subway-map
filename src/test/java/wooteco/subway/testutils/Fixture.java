package wooteco.subway.testutils;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.request.StationRequest;

public class Fixture {
    public static final LineRequest LINE_REQUEST_신분당선_STATION_1_2 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
    public static final LineRequest LINE_REQUEST_신분당선2_FOR_PUT = new LineRequest("신분당선2", "bg-red-600");
    public static final LineRequest LINE_REQUEST_분당선_STATION_1_3 = new LineRequest("분당선", "bg-red-601", 1L, 3L, 12);
    public static final LineRequest LINE_REQUEST_중앙선_STATION_1_3 = new LineRequest("중앙선", "bg-red-602", 1L, 3L, 12);
    public static final LineRequest LINE_REQUEST_2호선_STATION_1_3 = new LineRequest("2호선", "bg-red-603", 1L, 3L, 12);
    public static final StationRequest STATION_REQUEST_강남역 = new StationRequest("강남역");
    public static final StationRequest STATION_REQUEST_잠실역 = new StationRequest("잠실역");
    public static final StationRequest STATION_REQUEST_역삼역 = new StationRequest("역삼역");
    public static final StationRequest STATION_REQUEST_신림역 = new StationRequest("신림역");
    public static final StationRequest STATION_REQUEST_서울대역 = new StationRequest("서울대역");
    public static final Line LINE_1_BLUE = new Line("1호선", "blue");
    public static final Line LINE_2_GREEN = new Line("2호선", "green");
    public static final Section LINE_1_SECTION_A = new Section(1L, 1L, 2L, 1);
    public static final Section LINE_1_SECTION_B = new Section(1L, 1L, 3L, 2);
    public static final Station STATION_선릉 = new Station("선릉역");
    public static final Station STATION_1_강남 = new Station(1L, "강남역");
}
