package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptance.AcceptanceFixture.LINE_URL;
import static wooteco.subway.acceptance.AcceptanceFixture.STATION_URL;
import static wooteco.subway.acceptance.AcceptanceFixture.deleteMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.postMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.강남역;
import static wooteco.subway.acceptance.AcceptanceFixture.신분당선;
import static wooteco.subway.acceptance.AcceptanceFixture.양재역;
import static wooteco.subway.acceptance.AcceptanceFixture.청계산입구역;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class SectionAcceptanceTest extends AcceptanceTest {

    @Nested
    @DisplayName("구간 등록")
    class register extends AcceptanceTest {

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
    class deleteSection extends AcceptanceTest {

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
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
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
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
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
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
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
