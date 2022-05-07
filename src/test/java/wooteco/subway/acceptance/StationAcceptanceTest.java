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
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private final Map<String, String> station = getStation("강남역");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        ExtractableResponse<Response> response = getResponse(setRequest(station).post("/stations"));

        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location"))
                .isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        setRequest(station).post("/stations");

        // when
        ExtractableResponse<Response> response = getResponse(setRequest(station).post("/stations"));

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
                .isEqualTo("이미 해당 이름의 역이 있습니다.");
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Map<String, String> station2 = getStation("역삼역");
        String uri1 = getResponse(setRequest(station).post("/stations"))
                .header("Location");

        String uri2 = getResponse(setRequest(station2).post("/stations"))
                .header("Location");

        List<Long> expectedLineIds = Stream.of(uri1, uri2)
                .map(it -> Long.parseLong(it.split("/")[2]))
                .collect(Collectors.toList());

        // when
        ExtractableResponse<Response> response = getResponse(setRequest(station2).get("/stations"));

        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds)
                .containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        String uri = getResponse(setRequest(station).post("/stations"))
                .header("Location");

        // when
        ExtractableResponse<Response> response = getResponse(setRequest().delete(uri));

        // then
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철역을 제거한다.")
    @Test
    void deleteStation_error() {
        ExtractableResponse<Response> response = getResponse(setRequest().delete("/stations/100"));
        assertThat(response.statusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
                .isEqualTo("해당 아이디의 역이 없습니다.");
    }

    private Map<String, String> getStation(String name) {
        Map<String, String> stationMap = new HashMap<>();
        stationMap.put("name", name);
        return stationMap;
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
