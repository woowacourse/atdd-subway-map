package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Sql("classpath:tableInit.sql")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선 한개가 저장된다.")
    @Test
    void create() {
        ExtractableResponse<Response> response = createLine("2호선", "bg-green-600");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        createLine("2호선", "bg-green-600");

        ExtractableResponse<Response> response = createLine("2호선", "bg-green-600");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = createLine("2호선", "bg-green-600");
        ExtractableResponse<Response> createResponse2 = createLine("3호선", "bg-orange-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("id를 이용하여 지하철역을 조회한다.")
    @Test
    public void getLine() {
        /// given
        createLine("2호선", "bg-green-600");
        createLine("3호선", "bg-orange-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/1")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(1))
            .body("name", is("2호선"))
            .body("color", is("bg-green-600"))
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("id를 기준으로 노선을 수정한다.")
    @Test
    public void putLine() {
        /// given
        createLine("2호선", "bg-green-600");

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "3호선");
        params2.put("color", "bg-orange-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params2)
            .when()
            .put("/lines/1")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("id를 이용해 노선을 삭제한다")
    @Test
    public void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createLine("2호선", "bg-green-600");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }
}