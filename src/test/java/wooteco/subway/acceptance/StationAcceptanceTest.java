package wooteco.subway.acceptance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import wooteco.subway.controller.dto.StationResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    @Override
    public void setUp() {
        RestAssured.port = super.port;
    }

    @DisplayName("지하철역 생성 성공")
    @Test
    void createStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철역 생성 실패 - 기존에 존재하는 지하철역 이름으로 지하철역을 생성")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        RestAssuredHelper.jsonPost(params, "/stations");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonPost(params, "/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 조회 성공")
    @Test
    void getStations() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        ExtractableResponse<Response> createResponse1 = RestAssuredHelper.jsonPost(params1, "/stations");

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "역삼역");
        ExtractableResponse<Response> createResponse2 = RestAssuredHelper.jsonPost(params2, "/stations");

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.jsonGet("/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                                           .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                                           .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                                           .getList(".", StationResponse.class)
                                           .stream()
                                           .map(StationResponse::getId)
                                           .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역 제거 성공")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        ExtractableResponse<Response> createResponse = RestAssuredHelper.jsonPost(params, "/stations");

        // when
        String createdLocation = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssuredHelper.jsonDelete(createdLocation);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
