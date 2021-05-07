package wooteco.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.repository.StationRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@Transactional
public class StationAcceptanceTest extends AcceptanceTest {

    @Autowired
    private StationRepository stationRepository;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> param = new HashMap<>();
        param.put("name", "강남역");

        // when
        ExtractableResponse<Response> response = createPostResponse(param);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> 강남역 = new HashMap<>();
        강남역.put("name", "강남역");
        createPostResponse(강남역);

        // when
        ExtractableResponse<Response> response = createPostResponse(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Map<String, String> 강남역 = new HashMap<>();
        강남역.put("name", "강남역");
        ExtractableResponse<Response> createResponse1 = createPostResponse(강남역);

        Map<String, String> 역삼역 = new HashMap<>();
        역삼역.put("name", "역삼역");
        ExtractableResponse<Response> createResponse2 = createPostResponse(역삼역);

        // when
        ExtractableResponse<Response> response = createGetResponse();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath()
                .getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> 강남역 = new HashMap<>();
        강남역.put("name", "강남역");
        ExtractableResponse<Response> createResponse = createPostResponse(강남역);

        int originalSize = stationRepository.findAll().size();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = createDeleteResponse(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(stationRepository.findAll()).hasSize(originalSize - 1);
    }

    private ExtractableResponse<Response> createPostResponse(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createDeleteResponse(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createGetResponse() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }
}
