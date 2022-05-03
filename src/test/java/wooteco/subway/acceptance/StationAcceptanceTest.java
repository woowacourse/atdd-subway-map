package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.StationResponse;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("/stations에 대한 인수테스트")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("POST /stations - 지하철역 생성 테스트")
    @Nested
    class CreateStationTest extends AcceptanceTest {

        @Test
        void 지하철역을_생성한다() {
            Map<String, String> params = new HashMap<>() {{
                put("name", "강남역");
            }};

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/stations")
                    .then().log().all()
                    .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
        }

        @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 예외가 발생한다.")
        @Test
        void createStationWithDuplicateName() {
            Map<String, String> params = new HashMap<>() {{
                put("name", "강남역");
            }};
            postStation(params);

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/stations")
                    .then().log().all()
                    .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("GET /stations - 지하철역 조회 테스트")
    @Test
    void 지하철역을_조회한다() {
        ExtractableResponse<Response> createResponse1 = postStation(new HashMap<>() {{
            put("name", "강남역");
        }});
        ExtractableResponse<Response> createResponse2 = postStation(new HashMap<>() {{
            put("name", "역삼역");
        }});

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                .getList(".", StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsExactlyElementsOf(expectedLineIds);
    }

    @DisplayName("DELETE /stations/:id - 지하철역 제거 테스트")
    @Nested
    class DeleteStationTest extends AcceptanceTest {

        @Test
        void 지하철역을_제거한다() {
            Map<String, String> params = new HashMap<>() {{
                put("name", "강남역");
            }};
            ExtractableResponse<Response> createResponse = postStation(params);
            String uri = createResponse.header("Location");

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .when()
                    .delete(uri)
                    .then().log().all()
                    .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @DisplayName("존재하지 않는 id로 지하철역을 제거하려는 경우 예외가 발생한다.")
        @Test
        void deleteNonExistingStation() {
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .when()
                    .delete("/stations/1")
                    .then().log().all()
                    .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    private ExtractableResponse<Response> postStation(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
