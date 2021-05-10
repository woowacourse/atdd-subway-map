package wooteco.subway.line;


import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.web.dto.LineResponse;
import wooteco.subway.web.dto.StationResponse;

@DisplayName("노선 인수 테스트")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final Map<String, Object> DATA1 = new HashMap<>();
    private static final Map<String, Object> DATA2 = new HashMap<>();
    private static final Map<String, Object> DATA_FOR_UPDATE = new HashMap<>();
    private static final Map<String, Object> DATA_EMPTY_STRING = new HashMap<>();
    private static final Map<String, Object> DATA_NULL = new HashMap<>();

    private static final String LINES_PATH = "/lines/";
    private static final String STATIONS_PATH = "/stations/";
    private static final String LOCATION = "Location";

    private static final String NAME = "name";
    private static final String COLOR = "color";
    private static final String UP_STATION_ID = "upStationId";
    private static final String DOWN_STATION_ID = "downStationId";
    private static final String DISTANCE = "distance";

    private static final String EMPTY_STRING = " ";
    private static final long ZERO = 0L;
    private static final long INVALID_ID = Long.MAX_VALUE;

    static {
        put(DATA_EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, ZERO, ZERO, ZERO);
        put(DATA_NULL, null, null, null, null, null);
        DATA_FOR_UPDATE.put(NAME, "수정이름");
        DATA_FOR_UPDATE.put(COLOR, "수정 색");
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
        Long stationA = postStation(stationData("A역"));
        Long stationB = postStation(stationData("B역"));
        Long stationC = postStation(stationData("C역"));

        put(DATA1, "1호선", "bg-red-600", stationA, stationB, 3L);
        put(DATA2, "2호선", "bg-green-600", stationB, stationC, 5L);
    }

    private Map<String, Object> stationData(String name) {
        return new HashMap<String, Object>() {{
            put(NAME, name);
        }};
    }

    private Long postStation(Map<String, Object> data) {
        return getRequestSpecification()
                .body(data)
                .post(STATIONS_PATH)
                .as(StationResponse.class)
                .getId();
    }

    private static void put(Map<String, Object> data, String name, String color,
            Long upStationId, Long downStationId, Long distance) {
        data.put(NAME, name);
        data.put(COLOR, color);
        data.put(UP_STATION_ID, upStationId);
        data.put(DOWN_STATION_ID, downStationId);
        data.put(DISTANCE, distance);
    }

    @Test
    @DisplayName("노선 생성")
    void create() {
        // when
        ExtractableResponse<Response> response = postLine(DATA1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // todo nested하게 들어있는 stations까지 한번에 변환하려고 InnerClass로 만드뮤ㅠㅠ (LineResponse.StationResponse)
        LineResponse lineResponse = response.as(LineResponse.class);
        assertLineResponse(lineResponse, DATA1);

        Long up_station_id = (Long) DATA1.get(UP_STATION_ID);
        Long down_station_id = (Long) DATA1.get(DOWN_STATION_ID);

        List<Long> ids = lineResponse.getStations().stream()
                .map(stationResponse -> stationResponse.getId())
                .collect(Collectors.toList());

        assertThat(ids).containsExactly(up_station_id, down_station_id);
    }

    @Test
    @DisplayName("중복이름 노선 생성불가")
    void createFail_duplicatedName() {
        // when
        ExtractableResponse<Response> response1 = postLine(DATA1);
        ExtractableResponse<Response> response2 = postLine(DATA1);

        // then
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("name,color 빈 문자열: 노선 생성불가")
    void createFail_emptyString() {
        // when
        ExtractableResponse<Response> response = postLine(DATA_EMPTY_STRING);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("name,color null: 노선 생성불가")
    void createFail_null() {
        // when
        ExtractableResponse<Response> response = postLine(DATA_NULL);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("노선 목록 조회")
    void listLines() {
        /// given
        ExtractableResponse<Response> postResponse1 = postLine(DATA1);
        ExtractableResponse<Response> postResponse2 = postLine(DATA2);

        // when
        ExtractableResponse<Response> listResponse = listLine();

        // then
        assertThat(listResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<LineResponse> expectedLines = toLineDtos(postResponse1, postResponse2);
        LineResponse[] results = listResponse.as(LineResponse[].class);

        for (int i = 0; i < expectedLines.size(); i++) {
            LineResponse result = results[i];
            LineResponse expected = expectedLines.get(i);

            assertThat(result.getId()).isEqualTo(expected.getId());
            assertThat(result.getName()).isEqualTo(expected.getName());
            assertThat(result.getColor()).isEqualTo(expected.getColor());
        }
    }

    private List<LineResponse> toLineDtos(ExtractableResponse<Response>... responses) {
        return Arrays.stream(responses)
                .map(response -> response.as(LineResponse.class))
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("노선 수정")
    void updateLine() {
        // given
        ExtractableResponse<Response> postResponse = postLine(DATA1);

        // when
        String uri = postResponse.header(LOCATION);
        ExtractableResponse<Response> updateResponse = putLine(DATA_FOR_UPDATE, uri);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse lineResponse = getLine(uri).as(LineResponse.class);
        assertLineResponse(lineResponse, DATA_FOR_UPDATE);
    }

    @Test
    @DisplayName("이름에 빈 문자열: 노선 수정불가")
    void updateFail_emptyString() {
        // given
        ExtractableResponse<Response> response1 = postLine(DATA1);
        String uri = response1.header(LOCATION);

        // when
        ExtractableResponse<Response> putResponse = putLine(DATA_EMPTY_STRING, uri);

        // then
        assertThat(putResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("이름에 null: 노선 수정불가")
    void updateFail_null() {
        // given
        ExtractableResponse<Response> response1 = postLine(DATA1);
        String uri = response1.header(LOCATION);

        // when
        ExtractableResponse<Response> putResponse = putLine(DATA_NULL, uri);

        // then
        assertThat(putResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("존재하지 않는 노선 수정불가")
    void updateLineByInvalidId() {
        // when
        ExtractableResponse<Response> response = putLine(DATA_FOR_UPDATE, LINES_PATH + INVALID_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("노선 삭제")
    void deleteLine() {
        // given
        ExtractableResponse<Response> postResponse1 = postLine(DATA1);
        ExtractableResponse<Response> postResponse2 = postLine(DATA2);
        String uri = postResponse1.header(LOCATION);

        // when
        ExtractableResponse<Response> response = deleteLine(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        List<LineResponse> lineResponses = Arrays.asList(listLine().as(LineResponse[].class));

        assertThat(lineResponses.size()).isEqualTo(1);
        assertLineResponse(lineResponses.get(0), DATA2);
    }

    @Test
    @DisplayName("존재하지 않는 노선 삭제불가")
    void deleteLineByInvalidId() {
        // when
        ExtractableResponse<Response> response = deleteLine(LINES_PATH + INVALID_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> postLine(Map<String, Object> data) {
        return getRequestSpecification()
                .body(data)
                .post(LINES_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> listLine() {
        return getRequestSpecification()
                .get(LINES_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLine(String path) {
        return getRequestSpecification()
                .get(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> putLine(Map<String, Object> data, String path) {
        return getRequestSpecification()
                .body(data)
                .put(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(String path) {
        return getRequestSpecification()
                .delete(path)
                .then().log().all()
                .extract();
    }

    private RequestSpecification getRequestSpecification() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    private void assertLineResponse(LineResponse result, Map<String, Object> expected) {
        assertThat(result.getName()).isEqualTo(expected.get(NAME));
        assertThat(result.getColor()).isEqualTo(expected.get(COLOR));
    }
}
