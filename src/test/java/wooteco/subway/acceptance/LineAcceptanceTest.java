package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.exception.response.ErrorResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선 생성 성공")
    @Test
    void createLine() {

        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-green-600");
        params.put("name", "3호선");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse.getColor()).isEqualTo("bg-green-600");
        assertThat(lineResponse.getName()).isEqualTo("3호선");
        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo("역삼역");
    }

    @DisplayName("노선 생성 실패 - 존재하지 않는 역 ID")
    @Test
    void createLineWithDoesNotExistsStation() {

        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-green-600");
        params.put("name", "3호선");
        params.put("upStationId", "5");
        params.put("downStationId", "6");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("해당 ID와 일치하는 역이 존재하지 않습니다.");
    }

    @DisplayName("노선 생성 실패 - 기존에 존재하는 노선 이름으로 노선을 생성")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("이미 존재하는 노선 이름입니다.");
    }

    @DisplayName("노선 생성 실패 - RequestDto 의 속성이 null 값 일 경우 예외 발생")
    @Test
    void createLineWithNullField() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("널이어서는 안됩니다");
    }

    @DisplayName("모든 노선 조회 성공")
    @Test
    void getLines() {

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonGet("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);

        final LineResponse sinbundangResponse = lineResponses.get(0);
        assertThat(sinbundangResponse.getColor()).isEqualTo("bg-red-600");
        assertThat(sinbundangResponse.getName()).isEqualTo("신분당선");
        assertThat(sinbundangResponse.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(sinbundangResponse.getStations().get(1).getName()).isEqualTo("역삼역");

        final LineResponse secondLineResponse = lineResponses.get(1);
        assertThat(secondLineResponse.getColor()).isEqualTo("bg-green-600");
        assertThat(secondLineResponse.getName()).isEqualTo("2호선");
        assertThat(secondLineResponse.getStations().get(0).getName()).isEqualTo("선릉역");
        assertThat(secondLineResponse.getStations().get(1).getName()).isEqualTo("삼성역");
    }

    @DisplayName("단일 노선 조회 성공")
    @Test
    void getLine() {

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonGet("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo("역삼역");
    }

    @DisplayName("단일 노선 조회 실패 - 존재하지 않는 노선 조회")
    @Test
    void getLineThatDoesNotExists() {

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonGet("/lines/3");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("해당 ID와 일치하는 노선이 존재하지 않습니다.");
    }

    @DisplayName("노선 수정 성공")
    @Test
    void editLine() {

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-green-600");
        params2.put("name", "3호선");

        ExtractableResponse<Response> response = RestAssuredHelper.jsonPut(params2, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final LineResponse lineResponse = RestAssured.get("/lines/1").as(LineResponse.class);
        assertThat(lineResponse.getColor()).isEqualTo("bg-green-600");
        assertThat(lineResponse.getName()).isEqualTo("3호선");
    }

    @DisplayName("노선 수정 성공 - 노선의 이름은 그대로, 색상만을 수정")
    @Test
    void editOnlyColorOfLine() {

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-green-600");
        params2.put("name", "신분당선");
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPut(params2, "/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final LineResponse lineResponse = RestAssured.get("/lines/1").as(LineResponse.class);
        assertThat(lineResponse.getColor()).isEqualTo("bg-green-600");
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
    }

    @DisplayName("노선 수정 실패 - 노선을 중복된 이름으로 수정")
    @Test
    void editLineAsDuplicateName() {

        // when
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-green-600");
        params.put("name", "신분당선");
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPut(params, "/lines/2");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("이미 존재하는 노선 이름입니다.");
    }

    @DisplayName("노선 제거 성공")
    @Test
    void deleteLine() {

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonDelete("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
