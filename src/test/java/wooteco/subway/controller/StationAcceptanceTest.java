package wooteco.subway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import wooteco.subway.controller.request.StationRequest;
import wooteco.subway.controller.response.StationResponse;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.service.StationService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StationAcceptanceTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    int port;

    @Autowired
    private StationService stationService;

    private List<Long> testStationIds;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testStationIds = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        try {
            testStationIds.forEach(testStationId -> stationService.deleteById(testStationId));
        } catch (SubwayException ignored) {
        }
    }

    private ValidatableResponse createStation(StationRequest stationRequest) throws JsonProcessingException {
        String requestBody = OBJECT_MAPPER.writeValueAsString(stationRequest);
        ValidatableResponse validatableResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when().post("/stations")
                .then().log().all();
        int statusCode = validatableResponse.extract().statusCode();
        if (statusCode != HttpStatus.BAD_REQUEST.value()) {
            addCreatedStationId(validatableResponse);
        }
        return validatableResponse;
    }

    private void addCreatedStationId(ValidatableResponse validatableResponse) {
        String headerToken = validatableResponse.extract()
                .header("Location")
                .split("/")[2];
        long id = Long.parseLong(headerToken);
        testStationIds.add(id);
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() throws JsonProcessingException {
        StationRequest stationRequest = new StationRequest("강남역");
        ValidatableResponse validatableResponse = createStation(stationRequest);
        long id = testStationIds.get(0);

        StationResponse stationResponse = new StationResponse(id, "강남역");
        String responseBody = OBJECT_MAPPER.writeValueAsString(stationResponse);

        validatableResponse.statusCode(HttpStatus.CREATED.value())
                .body(is(responseBody));
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() throws JsonProcessingException {
        StationRequest stationRequest = new StationRequest("강남역");
        createStation(stationRequest);

        createStation(stationRequest)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() throws JsonProcessingException {
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("천호역");
        createStation(stationRequest1);
        createStation(stationRequest2);

        List<Long> resultStationIds = RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getList("", StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultStationIds.containsAll(testStationIds)).isTrue();
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() throws JsonProcessingException {
        StationRequest stationRequest = new StationRequest("강남역");
        String uri = createStation(stationRequest).extract()
                .header("Location");

        RestAssured.given().log().all()
                .when().delete(uri)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
