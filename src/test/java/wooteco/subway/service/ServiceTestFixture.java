package wooteco.subway.service;

import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.StationRequest;

public class ServiceTestFixture {
    public static StationRequest 선릉역_요청 = new StationRequest("선릉");
    public static StationRequest 잠실역_요청 = new StationRequest("잠실");
    public static StationRequest 동두천역_요청 = new StationRequest("동두천");
    public static StationRequest 지행역_요청 = new StationRequest("지행");

    public static Station 선릉역 = new Station(1L, "선릉");
    public static Station 잠실역 = new Station(2L, "잠실");

    public static final LineRequest.Put 이호선_수정 = new LineRequest.Put("2호선", "blue");
    public static final LineRequest.Put 수인분당선_수정 = new LineRequest.Put("수인분당선", "blue");

    public static LineRequest.Post 일호선_생성(long upStationId, long downStationId){
        return new LineRequest.Post("1호선", "blue", upStationId, downStationId, 10);
    }
    public static LineRequest.Post 이호선_생성(long upStationId, long downStationId){
        return new LineRequest.Post("2호선", "green", upStationId, downStationId, 10);
    }
    public static LineRequest.Post 경의중앙_생성(long upStationId, long downStationId){
        return new LineRequest.Post("경의중앙선", "sky", upStationId, downStationId, 10);
    }
}
