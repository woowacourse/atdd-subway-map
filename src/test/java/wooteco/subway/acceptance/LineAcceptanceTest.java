package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private final Map<String, String> line = getLine("신분당선", "bg-red-600");
    private final Map<String, String> line2 = getLine("분당선", "bg-green-600");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        ExtractableResponse<Response> response = getResponse(setRequest(line).post("/lines"));

        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location"))
                .isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        setRequest(line).post("/lines");

        // when
        ExtractableResponse<Response> response = getResponse(setRequest(line).post("/lines"));

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
                .isEqualTo("이미 해당 이름의 노선이 있습니다.");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        String uri1 = getResponse(setRequest(line).post("/lines"))
                .header("Location");

        String uri2 = getResponse(setRequest(getLine("분당선", "bg-green-600")).post("/lines"))
                .header("Location");

        List<Long> expectedLineIds = Stream.of(uri1, uri2)
                .map(it -> Long.parseLong(it.split("/")[2]))
                .collect(Collectors.toList());

        // when
        ExtractableResponse<Response> findAllResponse = getResponse(setRequest().get("/lines"));

        List<Long> resultLineIds = findAllResponse.jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        // then
        assertThat(findAllResponse.statusCode())
                .isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds)
                .containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        setRequest(line).post("/lines");

        // when
        ExtractableResponse<Response> updateResponse = getResponse(setRequest(line2)
                .put("/lines/1"));
        // then
        assertThat(updateResponse.statusCode())
                .isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 ID의 지하철 노선을 수정한다.")
    @Test
    void updateLine_error() {
        // given
        setRequest(line).post("/lines");

        // when
        ExtractableResponse<Response> updateResponse = getResponse(setRequest(line2)
                .put("/lines/2"));
        // then
        assertThat(updateResponse.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(updateResponse.body().asString())
                .isEqualTo("해당 아이디의 노선이 없습니다.");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        String uri = getResponse(setRequest(line).post("/lines"))
                .header("Location");

        // when
        ExtractableResponse<Response> response = getResponse(setRequest().delete(uri));

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거한다.")
    @Test
    void deleteLine_error() {
        ExtractableResponse<Response> response = getResponse(setRequest().delete("/lines/100"));
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
                .isEqualTo("해당 아이디의 노선이 없습니다.");
    }

    private Map<String, String> getLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

    private RequestSpecification setRequest(Map<String, String> body) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when();
    }

    private RequestSpecification setRequest() {
        return RestAssured.given().log().all().when();
    }

    private ExtractableResponse<Response> getResponse(Response response) {
        return response.then().log().all().extract();
    }
}
