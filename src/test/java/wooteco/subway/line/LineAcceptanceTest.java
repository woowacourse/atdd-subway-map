package wooteco.subway.line;


import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.web.dto.LineResponse;

@DisplayName("노선 인수 테스트")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final Map<String, String> lineData1 = new HashMap<>();
    private static final Map<String, String> lineData2 = new HashMap<>();
    private static final Map<String, String> lineDataForUpdate = new HashMap<>();

    static {
        lineData1.put("name", "신분당선");
        lineData1.put("color", "bg-red-600");
        lineData2.put("name", "2호선");
        lineData2.put("color", "bg-green-600");
        lineDataForUpdate.put("name", "수정된 이름");
        lineDataForUpdate.put("color", "수정된 색");
    }

    @Test
    @DisplayName("노선 생성")
    void create() {
        // when
        ExtractableResponse<Response> response = getRequestSpecification()
                .body(lineData1)
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        LineResponse lineResponse = response.body().jsonPath().getObject(".", LineResponse.class);
        compare(lineResponse, lineData1);
    }

    @Test
    @DisplayName("중복이름 노선 생성불가")
    void cannotCreateLineWithDuplicatedName() {
        // when
        ExtractableResponse<Response> response = getRequestSpecification()
                .body(lineData1)
                .post("/lines")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response2 = getRequestSpecification()
                .body(lineData1)
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("노선 목록 조회")
    void listLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = getRequestSpecification()
                .body(lineData1)
                .post("/lines")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> createResponse2 = getRequestSpecification()
                .body(lineData2)
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> listResponse = getLineListResponse();

        // then
        assertThat(listResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<LineResponse> expectedLines = Stream.of(createResponse1, createResponse2)
                .map(response -> response.jsonPath().getObject(".", LineResponse.class))
                .collect(Collectors.toList());

        List<LineResponse> results = new ArrayList<>(
                listResponse.jsonPath().getList(".", LineResponse.class));

        for (int i = 0; i < results.size(); i++) {
            LineResponse result = results.get(i);
            LineResponse expected = expectedLines.get(i);
            assertThat(result.getId()).isEqualTo(expected.getId());
            assertThat(result.getName()).isEqualTo(expected.getName());
            assertThat(result.getColor()).isEqualTo(expected.getColor());
        }
    }

    @Test
    @DisplayName("노선 수정")
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = getRequestSpecification()
                .body(lineData1)
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> updateResponse = getRequestSpecification()
                .body(lineDataForUpdate)
                .put(createResponse.header("Location"))
                .then().log().all()
                .extract();

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> resultResponse = getRequestSpecification()
                .get(createResponse.header("Location"))
                .then().log().all()
                .extract();

        LineResponse lineResponse = resultResponse.jsonPath().getObject(".", LineResponse.class);
        compare(lineResponse, lineDataForUpdate);
    }

    @Test
    @DisplayName("존재하지 않는 노선 수정불가")
    void updateLineByInvalidId() {
        long invalidId = -1L;
        ExtractableResponse<Response> response = getRequestSpecification()
                .body(lineDataForUpdate)
                .put("/lines/" + invalidId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("노선 삭제")
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = getRequestSpecification()
                .body(lineData1)
                .post("/lines")
                .then().log().all()
                .extract();

        getRequestSpecification()
                .body(lineData2)
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = getRequestSpecification()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> lineListResponse = getLineListResponse();
        assertThat(lineListResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<LineResponse> lineResponses = lineListResponse.jsonPath()
                .getList(".", LineResponse.class);
        assertThat(lineResponses.size()).isEqualTo(1);
        compare(lineResponses.get(0), lineData2);
    }

    @Test
    @DisplayName("존재하지 않는 노선 삭제불가")
    void deleteLineByInvalidId() {
        long invalidId = -1L;
        ExtractableResponse<Response> response = getRequestSpecification()
                .delete("/lines/" + invalidId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> getLineListResponse() {
        return getRequestSpecification()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private RequestSpecification getRequestSpecification() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    private void compare(LineResponse result, Map<String, String> expected) {
        assertThat(result.getName()).isEqualTo(expected.get("name"));
        assertThat(result.getColor()).isEqualTo(expected.get("color"));
    }
}
