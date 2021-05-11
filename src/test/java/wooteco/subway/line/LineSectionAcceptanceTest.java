package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LineSectionAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    void setUpStations() {
        // given
        Map<String, String> station1 = new HashMap<>();
        station1.put("name", "강남역");
        sendPostRequest(station1, "/stations");

        Map<String, String> station2 = new HashMap<>();
        station2.put("name", "성수역");
        sendPostRequest(station2, "/stations");

        Map<String, String> station3 = new HashMap<>();
        station3.put("name", "잠실나루역");
        sendPostRequest(station3, "/stations");
    }

    private ExtractableResponse<Response> sendPostRequest(Map<String, ?> body, String requestPath) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(requestPath)
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철 노선을 구간과 함께 생성한다")
    @Test
    void createLine() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", "1000");
        //when
        ExtractableResponse<Response> response = sendPostRequest(params, "/lines");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("미리 등록되어 있지 않은 역은, 노선 생성에 사용될 수 없다.")
    @Test
    void createLineException() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 100);
        params.put("downStationId", 120);
        params.put("distance", "1000");
        //when
        ExtractableResponse<Response> response = sendPostRequest(params, "/lines");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역, 하행역, 거리가 다 채워져서 요청되지 않았다면 예외처리를 한다")
    @Test
    void createLineCheckFullParam() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 100);
        params.put("distance", "1000");
        //when
        ExtractableResponse<Response> response = sendPostRequest(params, "/lines");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("upstationid와 downstationid가 같다면 예외처리를 한다")
    @Test
    void createLineWithSameStationId() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 1);
        params.put("downStationId", 1);
        params.put("distance", "1000");
        //when
        ExtractableResponse<Response> response = sendPostRequest(params, "/lines");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 생성 시 distance가 0이하라면 예외처리한다")
    @Test
    void createLineDistanceCheck() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", "-10");
        //when
        ExtractableResponse<Response> response = sendPostRequest(params, "/lines");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
