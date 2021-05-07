package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clear() {
        jdbcTemplate.update("TRUNCATE TABLE line;");

        jdbcTemplate.update("INSERT INTO station(name) VALUES('강남역');");
        jdbcTemplate.update("INSERT INTO station(name) VALUES('역삼역');");
        jdbcTemplate.update("INSERT INTO station(name) VALUES('잠실역');");
    }

    @DisplayName("노선 생성 - 성공")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("노선 생성 - 실패(상행x)")
    @Test
    void createLineFailureWhenNoUpstationExists() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("upStationId", "4");
        params.put("downStationId", "2");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 생성 - 실패(하행x)")
    @Test
    void createLineFailureWhenNoDownStationExists() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("upStationId", "1");
        params.put("downStationId", "4");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("노선 생성 - 실패(상행==하행)")
    @Test
    void createLineFailureWhenDownStationIsSameAsUpstation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("upStationId", "2");
        params.put("downStationId", "2");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 생성 - 실패(null)")
    @CsvSource(value = {"null,null,null,null", "4,null,2,3", "4,1,null,3", "4,1,2,null", "null,1,2,3"},
            nullValues = {"null"})
    void createLineFailureWhenNullProperties(String name, String upStationId, String downStationId, String color) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("upStationId", "2");
        params.put("downStationId", "2");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 생성 - 실패(empty)")
    @CsvSource(value = {",,,", "4,,2,3", "4,1,,3", "4,1,2,", ",1,2,3"})
    void createLineFailureWhenEmptyProperties(String name, String upStationId, String downStationId, String color) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("upStationId", "2");
        params.put("downStationId", "2");
        params.put("color", "red");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("개별 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("upStationId", "3");
        params1.put("downStationId", "2");
        params1.put("color", "red");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getId()).isEqualTo(1);
    }

    @DisplayName("전체 노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("upStationId", "3");
        params1.put("downStationId", "2");
        params1.put("color", "red");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "3호선");
        params2.put("upStationId", "1");
        params2.put("downStationId", "2");
        params2.put("color", "red");
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "3호선");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("color", "red");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

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

    @DisplayName("노선 수정 - 성공")
    @Test
    void updateStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "3호선");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("color", "red");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("name", "2호선");
        updateParams.put("color", "blue");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.jsonPath().getObject(".", LineResponse.class).getColor()).isEqualTo("blue");
        assertThat(findResponse.jsonPath().getObject(".", LineResponse.class).getName()).isEqualTo("2호선");
    }

    @DisplayName("노선 수정 - 성공(원본 이름 변경 없을시)")
    @Test
    void updateStationWithOriginalName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "3호선");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("color", "red");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("name", "3호선");
        updateParams.put("color", "blue");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> findResponse = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findResponse.jsonPath().getObject(".", LineResponse.class).getColor()).isEqualTo("blue");
        assertThat(findResponse.jsonPath().getObject(".", LineResponse.class).getName()).isEqualTo("3호선");
    }

    @DisplayName("노선 수정 - 실패(중복 노선 이름)")
    @Test
    void updateStationFailureWhenThereIsDuplicateName() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "3호선");
        params1.put("upStationId", "1");
        params1.put("downStationId", "2");
        params1.put("color", "red");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("upStationId", "1");
        params2.put("downStationId", "2");
        params2.put("color", "red");
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse1.header("Location");
        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("name", "2호선");
        updateParams.put("color", "blue");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
