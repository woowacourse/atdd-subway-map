package wooteco.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void create_성공() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> result = 역_생성(강남역);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(result.header("Location")).isNotBlank();
    }


    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void create_실패_중복이름() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        역_생성(강남역);

        // when
        ExtractableResponse<Response> result = 역_생성(강남역);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void read_성공() {
        /// given
        StationRequest 강남역 = new StationRequest("강남역");
        ExtractableResponse<Response> 강남역_생성 = 역_생성(강남역);

        StationRequest 역삼역 = new StationRequest("역삼역");
        ExtractableResponse<Response> 역삼역_생성 = 역_생성(역삼역);
        List<Long> expectedLineIds = Arrays.asList(강남역_생성, 역삼역_생성).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        // when
        ExtractableResponse<Response> result = 역_불러오기();
        List<Long> resultLineIds = result.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }


    @DisplayName("지하철역을 제거한다.")
    @Test
    void delete_성공() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        ExtractableResponse<Response> 강남역_생성 = 역_생성(강남역);

        // when
        String uri = 강남역_생성.header("Location");
        ExtractableResponse<Response> result = 역_삭제(uri);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 역_불러오기() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 역_생성(StationRequest 강남역) {
        return RestAssured.given().log().all()
                .body(강남역)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 역_삭제(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
