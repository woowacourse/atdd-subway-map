package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.RestAssuredHelper;
import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.exception.response.ErrorResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선 생성 성공")
    @Test
    void createLine() {

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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("노선 생성 실패 - 존재하지 않는 상행역 ID")
    @Test
    void createLineWithDoesNotExistsStation() {

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
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("해당 상행역 ID에 해당하는 역이 존재하지 않습니다.");
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
        RestAssuredHelper.jsonPost(params, "/lines");

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
        RestAssuredHelper.jsonPost(params, "/lines");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("널이어서는 안됩니다");
    }

    @DisplayName("모든 노선 조회 성공")
    @Test
    void getLines() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        ExtractableResponse<Response> createResponse1 = RestAssuredHelper.jsonPost(params1, "/lines");

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-green-600");
        params2.put("name", "2호선");
        ExtractableResponse<Response> createResponse2 = RestAssuredHelper.jsonPost(params2, "/lines");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonGet("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                                           .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                                           .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                                           .getList(".", LineResponse.class)
                                           .stream()
                                           .map(LineResponse::getId)
                                           .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("단일 노선 조회 성공")
    @Test
    void getLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        ExtractableResponse<Response> createResponse = RestAssuredHelper.jsonPost(params, "/lines");

        // when
        String createdLocation = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssuredHelper.jsonGet(createdLocation);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
    }

    @DisplayName("단일 노선 조회 실패 - 존재하지 않는 노선 조회")
    @Test
    void getLineThatDoesNotExists() {
        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonGet("/lines/2");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("해당 ID에 해당하는 노선이 존재하지 않습니다.");
    }

    @DisplayName("노선 수정 성공")
    @Test
    void editLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        ExtractableResponse<Response> createResponse = RestAssuredHelper.jsonPost(params, "/lines");

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-green-600");
        params2.put("name", "2호선");
        String createdLocation = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPut(params2, createdLocation);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final LineResponse lineResponse = RestAssured.get(createdLocation).as(LineResponse.class);
        assertThat(lineResponse.getColor()).isEqualTo("bg-green-600");
        assertThat(lineResponse.getName()).isEqualTo("2호선");
    }

    @DisplayName("노선 수정 성공 - 노선의 이름은 그대로, 색상만을 수정")
    @Test
    void editOnlyColorOfLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        ExtractableResponse<Response> createResponse = RestAssuredHelper.jsonPost(params, "/lines");

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-green-600");
        params2.put("name", "신분당선");
        String createdLocation = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPut(params2, createdLocation);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final LineResponse lineResponse = RestAssured.get(createdLocation).as(LineResponse.class);
        assertThat(lineResponse.getColor()).isEqualTo("bg-green-600");
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
    }

    @DisplayName("노선 수정 실패 - 노선을 중복된 이름으로 수정")
    @Test
    void editLineAsDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        RestAssuredHelper.jsonPost(params, "/lines");

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-green-600");
        params2.put("name", "2호선");
        ExtractableResponse<Response> createResponse = RestAssuredHelper.jsonPost(params2, "/lines");

        // when
        Map<String, String> params3 = new HashMap<>();
        params3.put("color", "bg-green-600");
        params3.put("name", "신분당선");
        String createdLocation = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPut(params3, createdLocation);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getReason()).isEqualTo("이미 존재하는 노선 이름입니다.");
    }

    @DisplayName("노선 제거 성공")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        ExtractableResponse<Response> createResponse = RestAssuredHelper.jsonPost(params, "/lines");

        // when
        String createdLocation = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssuredHelper.jsonDelete(createdLocation);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
