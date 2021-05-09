package wooteco.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.Rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUpStations() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "강남역");
        Rest.post(params, "/stations");


        // given
        Map<String, Object> params2 = new HashMap<>();
        params2.put("name", "성수역");
        Rest.post(params2, "/stations");

    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", "1000");
        params.put("extraFare", "100");

        //when
        ExtractableResponse<Response> response = Rest.post(params, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void showLines() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", "1000");
        params.put("extraFare", "100");

        ExtractableResponse<Response> response = Rest.post(params, "/lines");

        //when
        ExtractableResponse<Response> getLinesResponse = Rest.get("/lines");

        //then
        assertThat(getLinesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Collections.singletonList(response).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = getLinesResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("노선을 조회한다")
    @Test
    void showLine() {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", "1000");
        params.put("extraFare", "100");

        ExtractableResponse<Response> response = Rest.post(params, "/lines");

        //when
        ExtractableResponse<Response> getLineResponse = Rest.get("/lines/1");

        //then
        LineResponse lineResponse = getLineResponse.jsonPath().getObject(".", LineResponse.class);

        assertThat(getLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo("테스트선");
        assertThat(lineResponse.getColor()).isEqualTo("red");
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", "1000");
        params.put("extraFare", "100");

        ExtractableResponse<Response> response = Rest.post(params, "/lines");

        //when
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("name", "수정한선");
        requestParam.put("color", "blue");

        ExtractableResponse<Response> putLineResponse = Rest.put(requestParam, "/lines/1");

        //then
        assertThat(putLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());


    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        //given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", "1000");
        params.put("extraFare", "100");

        ExtractableResponse<Response> response = Rest.post(params, "/lines");

        //when
        ExtractableResponse<Response> deleteLineResponse = Rest.delete("/lines/1");

        //then
        assertThat(deleteLineResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
