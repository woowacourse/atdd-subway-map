package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.SimpleRestAssured.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("/lines에 대한 인수테스트")
class LineAcceptanceTest extends AcceptanceTest {

    private static final StationResponse STATION_RESPONSE_1 = new StationResponse(1L, "강남역");
    private static final StationResponse STATION_RESPONSE_2 = new StationResponse(2L, "양재역");
    private static final StationResponse STATION_RESPONSE_3 = new StationResponse(3L, "가천대역");
    private static final StationResponse STATION_RESPONSE_4 = new StationResponse(4L, "태평역");

    @DisplayName("POST /lines - 지하철 노선 생성 테스트")
    @Nested
    class CreateLineTest extends AcceptanceTest {

        @Test
        void 성공시_201_CREATED() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));

            HashMap<String, Object> lineParams = makeLineJson("신분당선", "빨간색", 1L, 2L, 10);
            ExtractableResponse<Response> response = SimpleRestAssured.post(lineParams, "/lines");

            LineResponse actual = response.jsonPath().getObject(".", LineResponse.class);
            List<StationResponse> stations = List.of(new StationResponse(1L, "강남역"),
                new StationResponse(2L, "양재역"));
            LineResponse expected = new LineResponse(1L, "신분당선", "빨간색", stations);
            assertAll(() -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                assertThat(response.header("Location")).isNotBlank();
                assertThat(actual).isEqualTo(expected);
            });
        }

        @Test
        void 이미_존재하는_노선명_입력시_400_BAD_REQUEST() {
            HashMap<String, Object> params = makeLineJson("신분당선", "빨간색", 1L, 2L, 10);
            SimpleRestAssured.post(params, "/lines");

            ExtractableResponse<Response> response = SimpleRestAssured.post(params, "/lines");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 존재하지_않는_하행_종점_입력시_400_BAD_REQUEST() {
            postStation(makeStationJson("강남역"));
            HashMap<String, Object> params = makeLineJson("신분당선", "빨간색", 1L, 2L, 5);

            ExtractableResponse<Response> response = SimpleRestAssured.post(params, "/lines");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 존재하지_않는_상행_종점_입력시_400_BAD_REQUEST() {
            postStation(makeStationJson("강남역"));
            HashMap<String, Object> params = makeLineJson("신분당선", "빨간색", 2L, 1L, 5);

            ExtractableResponse<Response> response = SimpleRestAssured.post(params, "/lines");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("GET /lines - 지하철 노선 목록 조회 테스트")
    @Test
    void 성공시_200_OK() {
        postStation(makeStationJson("강남역"));
        postStation(makeStationJson("양재역"));
        postStation(makeStationJson("가천대역"));
        postStation(makeStationJson("태평역"));

        HashMap<String, Object> lineParams1 = makeLineJson("신분당선", "빨간색", 1L, 2L, 10);
        HashMap<String, Object> lineParams2 = makeLineJson("분당선", "노란색", 3L, 4L, 10);
        postLine(lineParams1);
        postLine(lineParams2);

        ExtractableResponse<Response> response = SimpleRestAssured.get("/lines");

        List<LineResponse> responseBody = response.jsonPath()
            .getList(".", LineResponse.class);
        List<LineResponse> expected = List.of(
            new LineResponse(1L, "신분당선", "빨간색", List.of(STATION_RESPONSE_1, STATION_RESPONSE_2)),
            new LineResponse(2L, "분당선", "노란색", List.of(STATION_RESPONSE_3, STATION_RESPONSE_4))
        );

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(responseBody).hasSize(2);
            assertThat(responseBody).isEqualTo(expected);
        });
    }

    @DisplayName("GET /lines/:id - 지하철 노선 조회 테스트")
    @Nested
    class ShowLineTest extends AcceptanceTest {

        @Test
        void 성공시_200_OK() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            ExtractableResponse<Response> response = SimpleRestAssured.get("/lines/1");

            LineResponse actual = response.jsonPath().getObject(".", LineResponse.class);
            LineResponse expected = new LineResponse(1L, "신분당선", "빨간색",
                List.of(STATION_RESPONSE_1, STATION_RESPONSE_2));
            assertAll(() -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(actual).isEqualTo(expected);
            });
        }

        @Test
        void 존재하지_않는_노선인_경우_404_NOT_FOUND() {
            ExtractableResponse<Response> response = SimpleRestAssured.get("/lines/1");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

    @DisplayName("PUT /lines/:id - 지하철 노선 수정 테스트")
    @Nested
    class UpdateLineTest extends AcceptanceTest {

        @Test
        void 성공시_200_OK() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));
            HashMap<String, String> params = makeLineUpdateJson("NEW 분당선", "빨간색");

            ExtractableResponse<Response> response = SimpleRestAssured.put(params, "/lines/1");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 수정하려는_지하철_노선이_존재하지_않는_경우_400_BAD_REQUEST() {
            HashMap<String, String> params = makeLineUpdateJson("NEW 분당선", "빨간색");

            ExtractableResponse<Response> response = SimpleRestAssured.put(params, "/lines/1");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 이미_존재하는_지하철_노선_이름으로_수정시_400_BAD_REQUEST() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));
            postLine(makeLineJson("NEW 분당선", "빨간색", 1L, 2L, 10));
            Map<String, String> params = makeLineUpdateJson("NEW 분당선", "빨간색");

            ExtractableResponse<Response> response = SimpleRestAssured.put(params, "/lines/1");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("DELETE /lines/:id - 지하철 노선 제거 테스트")
    @Nested
    class DeleteLineTest extends AcceptanceTest {

        @Test
        void 성공시_204_NO_CONTENT() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            ExtractableResponse<Response> response = SimpleRestAssured.delete("/lines/1");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void 삭제하려는_지하철_노선이_존재하지_않는_경우_BAD_REQUEST() {
            ExtractableResponse<Response> response = SimpleRestAssured.delete("/lines/1");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("POST /lines/:id/sections - 지하철 노선 구간 추가 테스트")
    @Nested
    class AddStationToLine extends AcceptanceTest {

        @Test
        void 성공시_200_OK() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postStation(makeStationJson("양재시민의숲역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            HashMap<String, Object> sectionParams = makeSectionJson(2L, 3L, 4);

            ExtractableResponse<Response> response = SimpleRestAssured.post(sectionParams,
                "/lines/1/sections");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 존재하지_않는_지하철역을_입력할_경우_BAD_REQUEST() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            HashMap<String, Object> sectionParams = makeSectionJson(2L, 3L, 4);
            ExtractableResponse<Response> response = SimpleRestAssured.post(sectionParams,
                "/lines/1/sections");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 상행역_하행역_둘다_노선에_등록된_경우_BAD_REQUEST() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            HashMap<String, Object> sectionParams = makeSectionJson(1L, 2L, 5);

            ExtractableResponse<Response> response = SimpleRestAssured.post(sectionParams,
                "/lines/1/sections");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 상행역_하행역_둘다_노선에_등록되지_않은_경우_BAD_REQUEST() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            HashMap<String, Object> params = makeSectionJson(3L, 4L, 5);
            ExtractableResponse<Response> response = SimpleRestAssured.post(params,
                "/lines/1/sections");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 기존_등록된_두_역_사이에_등록할_경우_기존_길이보다_크면_BAD_REQEUST() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postStation(makeStationJson("양재시민의숲"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 3L, 5));

            HashMap<String, Object> params = makeSectionJson(1L, 2L, 7);
            ExtractableResponse<Response> response = SimpleRestAssured.post(params,
                "/lines/1/sections");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 기존_등록된_두_역_사이에_등록할_경우_기존_길이보다_같으면_BAD_REQEUST() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postStation(makeStationJson("양재시민의숲"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 3L, 5));

            HashMap<String, Object> params = makeSectionJson(1L, 2L, 5);
            ExtractableResponse<Response> response = SimpleRestAssured.post(params,
                "/lines/1/sections");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("DELETE /lines/:id/sections?stationsId={stationId} - 지하철 노선 구간 삭제 테스트")
    @Nested
    class RemoveStationToLine extends AcceptanceTest {

        @Test
        void 성공시_200_OK() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postStation(makeStationJson("양재시민의숲"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 3L, 10));
            SimpleRestAssured.post(makeSectionJson(1L, 2L, 5), "/lines/1/sections");

            ExtractableResponse<Response> response = SimpleRestAssured.delete("/lines/1/sections?stationId=" + 2);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 입력한_노선이_존재하지_않는_경우_404_NOT_FOUND() {
            ExtractableResponse<Response> response = SimpleRestAssured.delete("/lines/1/sections?stationId=" + 2);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        void 노선에_등록되어_있지_않은_지하철역일_경우_BAD_REQUEST() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            ExtractableResponse<Response> response = SimpleRestAssured.delete("/lines/1/sections?stationId=" + 3);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 노선에_구간이_마지막_하나일_경우_제거하면_BAD_REQEUST() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));
            SimpleRestAssured.delete("/lines/1/sections?stationId=" + 1);

            ExtractableResponse<Response> response = SimpleRestAssured.delete("/lines/1/sections?stationId=" + 2);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }
}
