//package wooteco.subway.acceptance;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertAll;
//
//import io.restassured.RestAssured;
//import io.restassured.response.ExtractableResponse;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import wooteco.subway.domain.Section;
//import wooteco.subway.dto.LineRequest;
//import wooteco.subway.dto.StationRequest;
//
//@DisplayName("지하철 구간 관련 기능")
//public class SectionAcceptanceTest extends AcceptanceTest {
//
//    private Long upStationId;
//    private Long downStationId;
//    private LineRequest lineRequest;
//
//    @BeforeEach
//    void setup() {
//        upStationId = createStation(new StationRequest("아차산역"));
//        downStationId = createStation(new StationRequest("군자역"));
//        lineRequest = new LineRequest("5호선", "bg-purple-600", upStationId, downStationId, 10);
//    }
//
//
//
//    @DisplayName("노선을 생성한다.")
//    @Test
//    void createLine() {
//        // when
//        final ExtractableResponse<Response> response = RestAssured.given().log().all()
//                .body(lineRequest)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when()
//                .post("/lines")
//                .then().log().all()
//                .extract();
//
//        // then
//        assertAll(
//                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
//                () -> assertThat(response.header("Location")).isNotBlank()
//        );
//    }
//
//    private Long createStation(final StationRequest stationRequest) {
//        final ExtractableResponse<Response> response = RestAssured.given().log().all()
//                .body(stationRequest)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when()
//                .post("/stations")
//                .then().log().all()
//                .extract();
//
//        return Long.parseLong(response.header("Location").split("/")[2]);
//    }
//}
