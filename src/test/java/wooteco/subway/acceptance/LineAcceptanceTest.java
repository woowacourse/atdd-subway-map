package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.AcceptanceFixture.LINE_URL;
import static wooteco.subway.acceptance.AcceptanceFixture.STATION_URL;
import static wooteco.subway.acceptance.AcceptanceFixture.deleteMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.getExpectedLineIds;
import static wooteco.subway.acceptance.AcceptanceFixture.getMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.getResultLineIds;
import static wooteco.subway.acceptance.AcceptanceFixture.postMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.putMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.강남역;
import static wooteco.subway.acceptance.AcceptanceFixture.백석역;
import static wooteco.subway.acceptance.AcceptanceFixture.신분당선;
import static wooteco.subway.acceptance.AcceptanceFixture.양재역;
import static wooteco.subway.acceptance.AcceptanceFixture.일산역;
import static wooteco.subway.acceptance.AcceptanceFixture.청계산입구역;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선")
    @Nested
    class line extends AcceptanceTest {
        @BeforeEach
        void init_station() {
            postMethodRequest(강남역, STATION_URL);
            postMethodRequest(청계산입구역, STATION_URL);
            postMethodRequest(일산역, STATION_URL);
            postMethodRequest(백석역, STATION_URL);
        }

        @DisplayName("지하철 노선을 생성한다.")
        @Test
        void createStation() {
            // given
            Map<String, String> params = new HashMap<>();
            params.put("name", "분당선");
            params.put("color", "노랑이");
            params.put("upStationId", "1");
            params.put("downStationId", "2");
            params.put("distance", "10");

            // when
            ExtractableResponse<Response> response = postMethodRequest(params, LINE_URL);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                    () -> assertThat(response.header("Location")).isNotBlank(),
                    () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("분당선"),
                    () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo("노랑이")
            );
        }

        @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성하면 에러를 응답한다.")
        @Test
        void createStationWithDuplicateName() {
            // given
            Map<String, String> params = new HashMap<>();
            params.put("name", "분당선");
            params.put("color", "노랑이");
            params.put("upStationId", "1");
            params.put("downStationId", "2");
            params.put("distance", "10");
            postMethodRequest(params, LINE_URL);

            // when
            ExtractableResponse<Response> response = postMethodRequest(params, LINE_URL);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @DisplayName("지하철 노선 목록을 조회한다.")
        @Test
        void getLines() {
            /// given
            Map<String, String> params1 = new HashMap<>();
            params1.put("name", "분당선");
            params1.put("color", "노랑이");
            params1.put("upStationId", "1");
            params1.put("downStationId", "2");
            params1.put("distance", "10");

            ExtractableResponse<Response> createResponse1 = postMethodRequest(params1, LINE_URL);

            Map<String, String> params2 = new HashMap<>();
            params2.put("name", "경의중앙선");
            params2.put("color", "하늘이");
            params2.put("upStationId", "3");
            params2.put("downStationId", "4");
            params2.put("distance", "10");

            ExtractableResponse<Response> createResponse2 = postMethodRequest(params2, LINE_URL);

            // when
            ExtractableResponse<Response> response = getMethodRequest(LINE_URL);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            List<Long> expectedLineIds = getExpectedLineIds(List.of(createResponse1, createResponse2));
            List<Long> resultLineIds = getResultLineIds(response);
            assertThat(resultLineIds).containsAll(expectedLineIds);
        }

        @DisplayName("지하철 노선을 조회한다.")
        @Test
        void getLine() {
            //given
            Map<String, String> params1 = new HashMap<>();
            params1.put("name", "분당선");
            params1.put("color", "노랑이");
            params1.put("upStationId", "1");
            params1.put("downStationId", "2");
            params1.put("distance", "10");

            ExtractableResponse<Response> createResponse1 = postMethodRequest(params1, LINE_URL);

            //when
            ExtractableResponse<Response> response = getMethodRequest(LINE_URL + "/" +
                    createResponse1.header("Location").split("/")[2]);

            //then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                    () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("분당선"),
                    () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo("노랑이")
            );
        }

        @DisplayName("지하철 노선을 수정한다.")
        @Test
        void modifyLine() {
            //given
            Map<String, String> params1 = new HashMap<>();
            params1.put("name", "분당선");
            params1.put("color", "노랑이");
            params1.put("upStationId", "1");
            params1.put("downStationId", "2");
            params1.put("distance", "10");

            ExtractableResponse<Response> createResponse1 = postMethodRequest(params1, LINE_URL);

            //when
            Map<String, String> params2 = new HashMap<>();
            params2.put("name", "신분당선");
            params2.put("color", "빨강이");
            ExtractableResponse<Response> response = putMethodRequest(params2, LINE_URL + "/"
                    + createResponse1.header("Location").split("/")[2]);

            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @DisplayName("지하철 노선을 제거한다.")
        @Test
        void deleteStation() {
            // given
            Map<String, String> params = new HashMap<>();
            params.put("name", "신분당선");
            params.put("color", "빨강이");
            params.put("upStationId", "1");
            params.put("downStationId", "2");
            params.put("distance", "10");
            ExtractableResponse<Response> createResponse = postMethodRequest(params, LINE_URL);

            // when
            ExtractableResponse<Response> response = deleteMethodRequest(LINE_URL + "/" +
                    createResponse.header("Location").split("/")[2]);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @DisplayName("기존에 존재하지 않는 지하철 노선 ID로 지하철 노선을 조회하면 에러를 응답한다.")
        @Test
        void getLineWithNonExistId() {
            //when
            ExtractableResponse<Response> response = getMethodRequest(LINE_URL + "/100");

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Nested
    @DisplayName("구간 등록")
    class RegistSection extends AcceptanceTest {

        @BeforeEach
        void initStationAndLine() {
            postMethodRequest(강남역, STATION_URL);
            postMethodRequest(청계산입구역, STATION_URL);
            postMethodRequest(양재역, STATION_URL);

            postMethodRequest(신분당선, LINE_URL);
        }

        @Test
        @DisplayName("상행 갈래길 구간 등록")
        void addSection() {
            //given
            Map<String, String> params = new HashMap<>();
            params.put("upStationId", "1");
            params.put("downStationId", "3");
            params.put("distance", "5");
            //when
            final ExtractableResponse<Response> response = postMethodRequest(params,
                    LINE_URL + "/1/sections");
            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("하행 갈래길 구간 등록")
        void addSectionWithDownBranch() {
            //given
            Map<String, String> params = new HashMap<>();
            params.put("upStationId", "3");
            params.put("downStationId", "2");
            params.put("distance", "5");
            //when
            final ExtractableResponse<Response> response = postMethodRequest(params,
                    LINE_URL + "/1/sections");
            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("갈래 구간을 등록하는데 거리가 기존 구간보다 크거나 같으면 400 에러")
        void addSectionWithBiggerDistance() {
            //given
            Map<String, String> params = new HashMap<>();
            params.put("upStationId", "1");
            params.put("downStationId", "3");
            params.put("distance", "10");
            //when
            final ExtractableResponse<Response> response = postMethodRequest(params,
                    LINE_URL + "/1/sections");
            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("상행 종점에 구간을 추가한다.")
        void addSectionOnUpTerminal() {
            //given
            Map<String, String> params = new HashMap<>();
            params.put("upStationId", "3");
            params.put("downStationId", "1");
            params.put("distance", "12");
            //when
            final ExtractableResponse<Response> response = postMethodRequest(params,
                    LINE_URL + "/1/sections");
            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("하행 종점에 구간을 추가한다.")
        void addSectionOnDownTerminal() {
            //given
            Map<String, String> params = new HashMap<>();
            params.put("upStationId", "2");
            params.put("downStationId", "3");
            params.put("distance", "12");
            //when
            final ExtractableResponse<Response> response = postMethodRequest(params,
                    LINE_URL + "/1/sections");
            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }
    }

    @Nested
    @DisplayName("구간 삭제")
    class DeleteSection extends AcceptanceTest {

        @BeforeEach
        void initStationAndLine() {
            postMethodRequest(강남역, STATION_URL);
            postMethodRequest(청계산입구역, STATION_URL);
            postMethodRequest(양재역, STATION_URL);

            postMethodRequest(신분당선, LINE_URL);
        }

        @Test
        @DisplayName("구간이 2개일 때 상행 종점을 제거한다.")
        void deleteUpTerminal() {
            //given
            Map<String, String> params = new HashMap<>();
            params.put("upStationId", "1");
            params.put("downStationId", "3");
            params.put("distance", "5");
            postMethodRequest(params, LINE_URL + "/1/sections");
            //when
            final ExtractableResponse<Response> response = deleteMethodRequest(LINE_URL +
                    "/1/sections?stationId=1");
            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("구간이 2개일 때 하행 종점을 제거한다.")
        void deleteDownTerminal() {
            //given
            Map<String, String> params = new HashMap<>();
            params.put("upStationId", "2");
            params.put("downStationId", "3");
            params.put("distance", "5");
            postMethodRequest(params, LINE_URL + "/1/sections");
            //when
            final ExtractableResponse<Response> response = deleteMethodRequest(LINE_URL +
                    "/1/sections?stationId=3");
            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("구간이 2개일 때 중간역을 제거한다.")
        void deleteSectionOnMiddleLine() {
            //given
            Map<String, String> params = new HashMap<>();
            params.put("upStationId", "1");
            params.put("downStationId", "3");
            params.put("distance", "5");
            postMethodRequest(params, LINE_URL + "/1/sections");
            //when
            final ExtractableResponse<Response> response = deleteMethodRequest(LINE_URL +
                    "/1/sections?stationId=3");
            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("구간이 1개일 때 구간을 제거하면 400 에러가 발생한다.")
        void deleteWithSingleSection() {
            //when
            final ExtractableResponse<Response> response = deleteMethodRequest(LINE_URL +
                    "/1/sections?stationId=1");
            //then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }
}
