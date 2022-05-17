package wooteco.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.dto.LineRequest;

import static wooteco.subway.utils.FixtureUtils.*;

public class LineFixtureUtils {

    private LineFixtureUtils() {
    }

    public static ExtractableResponse<Response> _7호선_및_역_생성요청() {
        ExtractableResponse<Response> 역_생성_응답_1 = post(STATION, 상도역);
        long 상도역_ID = extractId(역_생성_응답_1);

        ExtractableResponse<Response> 역_생성_응답_2 = post(STATION, 이수역);
        long 이수역_ID = extractId(역_생성_응답_2);

        LineRequest _7호선 = _7호선_생성();
        _7호선.setUpStationId(상도역_ID);
        _7호선.setDownStationId(이수역_ID);

        return post(LINE, _7호선);
    }

    public static ExtractableResponse<Response> 분당선_및_역_생성요청() {
        ExtractableResponse<Response> 역_생성_응답_1 = post(STATION, 강남구청역);
        long 강남구청역_ID = extractId(역_생성_응답_1);

        ExtractableResponse<Response> 역_생성_응답_2 = post(STATION, 선릉역);
        long 선릉역_ID = extractId(역_생성_응답_2);

        LineRequest 신분당선 = 신분당선_생성();
        신분당선.setUpStationId(강남구청역_ID);
        신분당선.setDownStationId(선릉역_ID);

        return post(LINE, 신분당선);
    }

    public static ExtractableResponse<Response> 노선_및_역_생성요청() {
        ExtractableResponse<Response> 상도역_생성_응답 = post(STATION, 상도역);
        long 상도역_ID = extractId(상도역_생성_응답);

        post(STATION, 이수역);

        ExtractableResponse<Response> 강남구청역_생성_응답 = post(STATION, 강남구청역);
        long 강남구청역_ID = extractId(강남구청역_생성_응답);

        LineRequest _7호선 = _7호선_생성();
        _7호선.setUpStationId(상도역_ID);
        _7호선.setDownStationId(강남구청역_ID);

        return post(LINE, _7호선);
    }
}
